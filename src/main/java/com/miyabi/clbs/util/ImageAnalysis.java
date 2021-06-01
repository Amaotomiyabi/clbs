package com.miyabi.clbs.util;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * com.miyabi.clbs.util
 *
 * @Author amotomiyabi
 * @Date 2020/05/01/
 * @Description
 */
public class ImageAnalysis {
    private static final int IMG_SIZE = 32;
    private static final int SMALL_IMG_SIZE = 8;
    private static ColorConvertOp convertOp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

    public static void main(String[] args) throws IOException {
        //getTagLists("deepdanbooru\\img\\").stream().forEach(System.out::println);
    }

    public static boolean imgCompare(InputStream is) {
        try (var ds = Files.newDirectoryStream(Path.of(ImageFormat.SAVE_PATH));
        ) {
            for (var entry : ds) {
                if (imgCompare(new FileInputStream(entry.toFile()), is) < 5) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static int getBlue(BufferedImage img, int x, int y) {
        return (img.getRGB(x, y)) & 0xff;
    }

    public static int isSexy(String rating) {
        return switch (rating) {
            case "rating:explicit" -> Constant.RATING_EXPLICIT;
            case "rating:questionable" -> Constant.RATING_QUESTIONABLE;
            default -> Constant.RATING_SAFE;
        };
    }

    public static List<String> analysisTag(String imgPath) {
        List<String> tags = new ArrayList<>();
        String[] cmd = {"deepdanbooru", "evaluate", imgPath,
                "--project-path", "deepdanbooru\\project", "--allow-folder"};
        try (var rbc = Channels.newChannel(Runtime.getRuntime().exec(cmd).getInputStream());
        ) {
            var buffer = ByteBuffer.allocateDirect(8192);
            while (rbc.read(buffer) != -1) {
                buffer.rewind();
                Charset.forName(System.getProperty("file.encoding"))
                        .decode(buffer)
                        .toString()
                        .lines()
                        .forEach(tags::add);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public static String builderCommand(List<String> imgPaths) {
        var sb = new StringBuilder();
        for (String entry : imgPaths) {
            sb.append("'");
            sb.append(imgPaths);
            sb.append("' ");
        }
        return sb.toString();
    }

    public static List<String> getTags(String imgPath) {
        return subList(analysisTag(imgPath));
    }

    public static List<List<String>> getTagLists(List<String> imgPaths) {
        return subLists(analysisTag(builderCommand(imgPaths)));
    }

    public static List<List<String>> subLists(List<String> list) {
        List<List<String>> tagLists = new ArrayList<>();
        int start = 0, end = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains("Tags of")) {
                start = i + 1;
                continue;
            }
            if (list.get(i).contains("rating")) {
                end = i + 1;
                tagLists.add(list.subList(start, end));
            }
        }
        return tagLists;
    }

    public static List<String> subList(List<String> list) {
        int start = 0, end = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains("Tags of")) {
                start = i + 1;
                break;
            }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).contains("rating")) {
                end = i + 1;
                break;
            }
        }
        return list.subList(start, end);
    }


    public static List<String> formatTags(List<String> list) {
        return list
                .subList(1, list.size() - 2)
                .parallelStream()
                .map(ImageAnalysis::formatTag)
                .collect(Collectors.toList());
    }

    public static String formatTag(String tag) {
        return tag.substring(8);
    }

    public static List<String> analysisTag2(String imgPath) {
        List<String> tags = new ArrayList<>();
        String[] cmd = {"deepdanbooru", "evaluate", "C:\\Users\\amotomiyabi\\Desktop\\deepdanbooru\\test",
                "--project-path", imgPath, "--allow-folder"};
        try (var bis = new BufferedReader(
                new InputStreamReader(Runtime.getRuntime().
                        exec(cmd).
                        getInputStream()));) {
            String s;
            while ((s = bis.readLine()) != null) {
                tags.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public static double imgCompare(InputStream img1, InputStream img2) throws IOException {
        return getHamming(getHash(img1), getHash(img2));
    }

    public static long getHash(InputStream imgStream) throws IOException {
        var img = ImageIO.read(imgStream);
        double[][] dctVals = FDct.
                fDctTransform(
                        grayscale(
                                resize(img, IMG_SIZE, IMG_SIZE)));
        return Long.parseLong(getImgHash(dctVals, getAvg(dctVals)), 2);
    }

    public static int getHamming(long var1, long var2) {
        var xor = var1 ^ var2;
        var count = 0;
        while (xor != 0) {
            xor = xor & (xor - 1);
            count++;
        }
        return count;
    }

    public static double getAvg(double[][] dctVals) {
        return Arrays.stream(dctVals)
                .parallel()
                .flatMapToDouble(Arrays::stream)
                .skip(1)
                .sum() / (SMALL_IMG_SIZE * SMALL_IMG_SIZE - 1);
    }

    public static String getImgHash(double[][] dctVals, double avg) {
        return Arrays.stream(dctVals)
                .parallel()
                .flatMapToDouble(Arrays::stream)
                .skip(1)
                .mapToObj(s -> s > avg ? "1" : "0")
                .collect(Collectors.joining());
    }

    public static double[][] grayscale(BufferedImage img) {
        convertOp.filter(img, img);
        double[][] vals = new double[IMG_SIZE][IMG_SIZE];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                vals[x][y] = getBlue(img, x, y);
            }
        }
        return vals;
    }

    public static BufferedImage resize(BufferedImage img, int width, int height) {
        var resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g = resizedImg.createGraphics();
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        return resizedImg;
    }


/*    public static void main(String[] args) {
        IntStream.range(0, 5)
                .mapToObj(ImageAnalysis::new)
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::runAsync)
                .collect(Collectors.toList()).forEach(CompletableFuture::join);
        System.out.println(System.currentTimeMillis() - s);
    }*/
}
