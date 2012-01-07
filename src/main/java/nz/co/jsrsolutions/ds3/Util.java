
package nz.co.jsrsolutions.ds3;

import java.util.Arrays;

class Util {

    public static <T> T[] concat(T[] first, T[] second) {

      T[] result = Arrays.copyOf(first, first.length + second.length);
      System.arraycopy(second, 0, result, first.length, second.length);

      return result;

    }

}