package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Author:BY
 * Date:2020/3/9
 * Description:图片添加水印  https://blog.csdn.net/chengjiangbo/article/details/44020673
 * 删除了 getset
 */
public class EditPicture {
    private final Logger logger = LoggerFactory.getLogger(EditPicture.class);

    public static void main(String[] args) {
        try {
            Image img = ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\解码4.jpg"));
            int width = img.getWidth(null);//得到源宽度
            int height = img.getHeight(null);//得到源高度
            System.out.println(width + " : " + height);
//            旋转任意度数包括直角(90,180,270,360)
//            MyImage2.rotateImage("C:\\Users\\冰忆\\Desktop\\500228199601226555.jpg", 180, null);
//            加文字水印,在源文件的基础上修改,位置不变
//            EditPicture.pressText("C:\\Users\\Administrator\\Desktop\\解码4.jpg", "人相对比商汤", "楷体", Font.BOLD, Color.blue, 0.5f);
//            加图片水印
//            MyImage2.pressImage("C:\\Users\\chengjiangbo\\Desktop\\images\\IMG_300_100.jpg", "C:\\Users\\chengjiangbo\\Desktop\\images\\QRCode.png", 300, 300, 0.5f);
//            生成缩略图
//            MyImage2 image2 = new MyImage2("C:\\Users\\chengjiangbo\\Desktop\\images\\IMG.jpg", "C:\\Users\\chengjiangbo\\Desktop\\images\\IMG_300_100.jpg");
//            image2.resizeFix(300, 100);//这里缩略图的比例在方法里会计算，防止失真。
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String srcFile;
    private String destFile;
    private int width;
    private int height;

    //    构造函数
    private EditPicture(String fileName, String newFileName) {
        this.srcFile = fileName;
        this.destFile = newFileName;
        Image img = null;
        try {
            img = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            logger.error("EditPicture 解析图片长宽异常: {}", e.getMessage());
        }
        width = img.getWidth(null);//得到源宽度
        height = img.getHeight(null);//得到源高度
    }

    public EditPicture() {
    }

    public String imageToBase64(File file) {
        String baseStr = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] read = new byte[fis.available()];
            int read1 = fis.read(read);
            baseStr = Base64.getEncoder().encodeToString(read);
        } catch (IOException e) {
            logger.error("EditPicture 文件转换base64异常: {}", e.getMessage());
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                logger.error("EditPicture 文件转换base64后流关闭异常: {}", e.getMessage());
            }
        }
        return baseStr;
    }

    //    强制压缩/放大图片到固定的大小
    private void resize(int newWidth, int newHeight) throws IOException {
        Image img = Toolkit.getDefaultToolkit().getImage(srcFile);
        BufferedImage bi_scale = toBufferedImage(img, newWidth, newHeight);
        //第二种通过图片流写入
        ImageIO.write(bi_scale, "JPEG", new File(destFile));
    }

    //    生成图片带红的处理方法
    private BufferedImage toBufferedImage(Image image, int width, int height) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // 确保图像中的所有像素都被加载,确定图像是否具有透明像素
        image = new ImageIcon(image).getImage();

        // 使用与屏幕兼容的格式创建缓冲图像
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        try {
            // 确定新缓冲图像的透明度类型
            int transparency = Transparency.OPAQUE;

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(width, height, transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // 使用默认颜色模型创建缓冲图像
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(width, height, type);
        }

        // 将图像复制到缓冲图像
        Graphics g = bimage.createGraphics();
        // 将图像绘制到缓冲图像上,绘制缩小的图
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return bimage;
    }

    //    按照固定的比例缩放图片
    public void resize(double t) throws IOException {
        int w = (int) (width * t);
        int h = (int) (height * t);
        resize(w, h);
    }

    //    已宽度为基准，等比例缩放图片
    private void resizeByWidth(int newWidth) throws IOException {
        int h = (int) (height * ((double) newWidth / width));
        resize(newWidth, h);
    }

    //    以高度为基准，等比例缩放图片
    private void resizeByHeight(int newHeight) throws IOException {
        int w = (int) (width * ((double) newHeight / height));
        resize(w, newHeight);
    }

    //    生成规格,重构大小
    public void resizeFix(int newWidth, int newHeight) throws IOException {
        if (width > height || (width / height > newWidth / newHeight)) {
            resizeByWidth(newWidth);
        } else {
            resizeByHeight(newHeight);
        }
    }

    /**
     * 添加图片水印
     *
     * @param targetImg 目标图片路径，如：C://myPictrue//1.jpg
     * @param waterImg  水印图片路径，如：C://myPictrue//logo.png
     * @param x         水印图片距离目标图片左侧的偏移量，如果x<0, 则在正中间
     * @param y         水印图片距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha     透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     */
    public static void pressImage(String targetImg, String waterImg, int x, int y, float alpha) throws Exception {
        try {
            File file = new File(targetImg);
            Image image = ImageIO.read(file);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            Image waterImage = ImageIO.read(new File(waterImg));//水印文件
            int width_wi = waterImage.getWidth(null);
            int height_wi = waterImage.getHeight(null);
            if (width <= width_wi || height <= height_wi) {
                throw new Exception("原图的宽、高必须大于水印图的宽、高");
            }
            AlphaComposite instance = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha);
            int widthDiff = width - width_wi;
            int heightDiff = height - height_wi;
            if (x < 0) {
                x = widthDiff / 2;
            } else if (x > widthDiff) {
                x = widthDiff;
            }
            if (y < 0) {
                y = heightDiff / 2;
            } else if (y > heightDiff) {
                y = heightDiff;
            }
            g.drawImage(waterImage, x, y, width_wi, height_wi, null); //水印文件结束
            g.dispose();
            ImageIO.write(bufferedImage, "JPEG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加文字水印
     *
     * @param targetImg 目标图片路径，如：C://myPictrue//1.jpg
     * @param pressText 水印文字， 如：中国证券网
     * @param fontName  字体名称，    如：宋体
     * @param fontStyle 字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
     * @param fontSize  字体大小，单位为像素
     * @param color     字体颜色
     *                  //     * @param x         水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
     *                  //     * @param y         水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha     透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     */
    public void pressText(String targetImg, String pressText, String fontName, int fontStyle, Color color, float alpha) {
        try {
            File file = new File(targetImg);
            Image image = ImageIO.read(file);
            int width = image.getWidth(null);//得到源宽度
            int height = image.getHeight(null);//得到源高度
            int x = width / 3; // 字体宽度起点
            int y = (int) (height * 0.85); // 字体高度起点
            int fontSize = width / 15; // 字体大小
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setColor(color);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            int width_wi = fontSize * getTextLength(pressText);
            int height_wi;
            height_wi = fontSize;
            int widthDiff = width - width_wi;
            int heightDiff = height - height_wi;
            if (x < 0) {
                x = widthDiff / 2;
            } else if (x > widthDiff) {
                x = widthDiff;
            }
            if (y < 0) {
                y = heightDiff / 2;
            } else if (y > heightDiff) {
                y = heightDiff;
            }
            g.drawString(pressText, x, y + height_wi);//水印文件
            g.dispose();
            ImageIO.write(bufferedImage, "JPEG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    计算文字像素长度
    private static int getTextLength(String text) {
        int textLength = text.length();
        int length = textLength;
        for (int i = 0; i < textLength; i++) {
            int wordLength = String.valueOf(text.charAt(i)).getBytes().length;
            if (wordLength > 1) {
                length += (wordLength - 1);
            }
        }
        return length % 2 == 0 ? length / 2 : length / 2 + 1;
    }

    //    旋转任意度数的方法
    private static void rotateImage(String targetImg, int degree, Color bgcolor) throws IOException {
        File file = new File(targetImg);
        BufferedImage sourceImage = ImageIO.read(file);
        int iw = sourceImage.getWidth();//原始图象的宽度
        int ih = sourceImage.getHeight();//原始图象的高度
        int w, h, x, y;
        degree = degree % 360;
        if (degree < 0)
            degree = 360 + degree;//将角度转换到0-360度之间
        double ang = Math.toRadians(degree);//将角度转为弧度
//        确定旋转后的图象的高度和宽度
        if (degree == 180 || degree == 0 || degree == 360) {
            w = iw;
            h = ih;
        } else if (degree == 90 || degree == 270) {
            w = ih;
            h = iw;
        } else {
            int d = iw + ih;
            w = (int) (d * Math.abs(Math.cos(ang)));
            h = (int) (d * Math.abs(Math.sin(ang)));
        }
        x = (w / 2) - (iw / 2);//确定原点坐标
        y = (h / 2) - (ih / 2);
        BufferedImage rotatedImage = new BufferedImage(w, h, sourceImage.getType());
        Graphics2D gs = (Graphics2D) rotatedImage.getGraphics();
        if (bgcolor == null) {
            rotatedImage = gs.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        } else {
            gs.setColor(bgcolor);
            gs.fillRect(0, 0, w, h);//以给定颜色绘制旋转后图片的背景
        }

        //有两种旋转使用方式，第一使用AffineTransformOp，第二使用Graphics2D
//	    AffineTransform at = new AffineTransform();
//	    at.rotate(ang, w / 2, h / 2);//旋转图象
//	    at.translate(x, y);
//	    AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
//	    op.filter(sourceImage, rotatedImage);
//	    sourceImage = rotatedImage;
//	    ImageIO.write(sourceImage, "PNG", file);//这里的格式化请使用PNG格式，否则旋转后会出现红眼效果

        BufferedImage bufferedImage = new BufferedImage(w, h, sourceImage.getType());
        Graphics2D g = bufferedImage.createGraphics();
        if (bgcolor == null) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(bgcolor);
        }
        g.fillRect(0, 0, w, h);//以给定颜色绘制旋转后图片的背景
        g.rotate(Math.toRadians(degree), w / 2, h / 2);
        g.translate(x, y);
        g.drawImage(sourceImage, 0, 0, null);
        g.dispose();
        ImageIO.write(bufferedImage, "JPEG", file); //这里的JPEG也可以是PNG
    }
}