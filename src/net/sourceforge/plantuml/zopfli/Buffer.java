/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under The MIT License (Massachusetts Institute of Technology License)
 * 
 * See http://opensource.org/licenses/MIT
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 *
 * Original Author:  Arnaud Roques
 */
/*
Copyright 2014 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author: eustas.ru@gmail.com (Eugene Klyuchnikov)
*/

package net.sourceforge.plantuml.zopfli;

public class Buffer {

  byte[] data;
  int size;
  private int bp;

  Buffer() {
    data = new byte[65536];
  }

  public byte[] getData() {
    return data;
  }
  
  public byte[] getResult() {
      byte[] copy = new byte[size];
      System.arraycopy(data,0,copy,0,size);
      return copy;
  }

  public int getSize() {
    return size;
  }

  void append(byte value) {
    if (size == data.length) {
      byte[] copy = new byte[size * 2];
      System.arraycopy(data, 0, copy, 0, size);
      data = copy;
    }
    data[size++] = value;
  }

  void addBits(int symbol, int length) {
    for (int i = 0; i < length; i++) {
      if (bp == 0) {
        append((byte) 0);
      }
      int bit = (symbol >> i) & 1;
      data[size - 1] |= bit << bp;
      bp = (bp + 1) & 7;
    }
  }

  void addHuffmanBits(int symbol, int length) {
    for (int i = 0; i < length; i++) {
      if (bp == 0) {
        append((byte) 0);
      }
      int bit = (symbol >> (length - i - 1)) & 1;
      data[size - 1] |= bit << bp;
      bp = (bp + 1) & 7;
    }
  }
}
