import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yanxujiang on 2020-06-29.
 */
@Slf4j
public class Test {
  public static void main(String[] args) throws Exception {
    File file = new File("./test.log");
    byte[] data = getFileByteData(file);
    createFile(data, "ret.log");
  }

  private static File createFile(byte[] retByteArr, String fileName) {
    OutputStream os = null;
    BufferedOutputStream bos = null;
    try {
      os = new FileOutputStream("." + File.separator + fileName);
      bos = new BufferedOutputStream(os);
      os.write(retByteArr, 0, retByteArr.length);
      os.flush();
    } catch (Exception e) {
      log.error("create file error");
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return new File(fileName);
  }

  public static byte[] getFileByteData(File file) {
    byte[] data;
    try (InputStream os = new FileInputStream(file)) {
      data = new byte[os.available()];
      os.read(data, 0, os.available());
    } catch (FileNotFoundException e) {
      data = null;
      log.error("file not found. [file={}]", file, e);
    } catch (IOException e) {
      data = null;
      log.error("inputStream operate error.", e);
    }
    return data;
  }

}
