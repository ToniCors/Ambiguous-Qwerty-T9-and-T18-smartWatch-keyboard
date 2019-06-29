package com.example.heisenberg.watchx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MSD {

   private String s1;
   private String s2;
   private int[][] d;


   public MSD(String s1Arg, String s2Arg) {
      this.s1 = s1Arg;
      this.s2 = s2Arg;
      this.buildMatrix();
   }

   private static int r(char a, char b) {
      return a == b?0:1;
   }

   private void buildMatrix() {
      this.d = new int[this.s1.length() + 1][this.s2.length() + 1];
      if(this.s1.length() != 0 && this.s2.length() != 0) {
         int i;
         for(i = 0; i < this.s1.length() + 1; this.d[i][0] = i++) {
            ;
         }

         int j;
         for(j = 0; j < this.s2.length() + 1; this.d[0][j] = j++) {
            ;
         }

         for(i = 1; i <= this.s1.length(); ++i) {
            for(j = 1; j <= this.s2.length(); ++j) {
               int a = this.d[i - 1][j] + 1;
               int b = this.d[i][j - 1] + 1;
               int c = this.d[i - 1][j - 1] + r(this.s1.charAt(i - 1), this.s2.charAt(j - 1));
               int m = Math.min(a, b);
               m = Math.min(m, c);
               this.d[i][j] = m;
            }
         }

      } else {
         this.d[this.s1.length()][this.s2.length()] = Math.max(this.s1.length(), this.s2.length());
      }
   }

   public int[][] getMatrix() {
      return this.d;
   }

   public int getMSD() {
      return this.d[this.s1.length()][this.s2.length()];
   }

   public String getS1() {
      return this.s1;
   }

   public String getS2() {
      return this.s2;
   }

   public double getErrorRate() {
      return (double)this.getMSD() / (double)Math.max(this.s1.length(), this.s2.length()) * 100.0D;
   }

   private void dumpMatrix() {
      int rows = this.d.length;
      int cols = this.d[0].length;
      String s11 = " " + this.s1;
      String s22 = " " + this.s2;
      System.out.print(" ");

      int i;
      for(i = 0; i < s22.length(); ++i) {
         System.out.print("  " + s22.substring(i, i + 1));
      }

      System.out.println();

      for(i = 0; i < rows; ++i) {
         System.out.print(s11.substring(i, i + 1));

         for(int j = 0; j < cols; ++j) {
            String f;
            for(f = String.valueOf(this.d[i][j]); f.length() < 3; f = " " + f) {
               ;
            }

            System.out.print(f);
         }

         if(i != rows - 1) {
            System.out.println();
         }
      }

      System.out.println();
   }

   /*public static void main(String[] args) throws IOException {
      boolean mOption = false;
      boolean aOption = false;
      boolean erOption = false;

      for(int stdin = 0; stdin < args.length; ++stdin) {
         if(args[stdin].equals("-m")) {
            mOption = true;
         } else if(args[stdin].equals("-a")) {
            aOption = true;
         } else if(args[stdin].equals("-er")) {
            erOption = true;
         } else if(args[stdin].equals("?")) {
            usage();
         } else {
            usage();
         }
      }

      BufferedReader var10 = new BufferedReader(new InputStreamReader(System.in), 1);
      System.out.println("============================");
      System.out.println("Minimum String Distance Demo");
      System.out.println("============================");
      System.out.println("Enter pairs of strings (^z to exit)");

      String s1;
      String s2;
      while((s1 = var10.readLine()) != null && (s2 = var10.readLine()) != null) {
         MSD s1s2 = new MSD(s1, s2);
         System.out.println("MSD = " + s1s2.getMSD());
         if(erOption) {
            System.out.println(String.format("Error rate (old) = %.4f%%", new Object[]{Double.valueOf(s1s2.getErrorRate())}));
            System.out.println(String.format("Error rate (new) = %.4f%%", new Object[]{Double.valueOf(s1s2.getErrorRateNew())}));
         }

         if(mOption) {
            s1s2.dumpMatrix();
         }

         if(aOption) {
            StringPair[] sp = s1s2.getAlignments();
            if(sp == null) {
               System.out.println("Outlier!  Alignments not available");
            } else {
               System.out.println("Alignments: " + sp.length + ", " + "mean size: " + s1s2.meanAlignmentSize());

               for(int i = 0; i < sp.length; ++i) {
                  if(i > 0) {
                     System.out.println("");
                  }

                  System.out.println(sp[i].s1);
                  System.out.println(sp[i].s2);
               }

               System.out.println("-------------");
            }
         }
      }

   }*/

   private static void usage() {
      String usageString = "usage: java MSD [-m] [-k] [-er]\n\nwhere -m  = output the MSD matrix\n      -a  = output the set of optimal alignments\n      -er = output the error rate";
      System.out.println(usageString);
      System.exit(0);
   }

   private static StringPair[] DoubleConcat(StringPair[] a, char c1, char c2, StringPair[] b) {
      StringPair[] returnvalue = new StringPair[a.length + b.length];

      int i;
      for(i = 0; i < a.length + b.length; ++i) {
         returnvalue[i] = new StringPair();
      }

      for(i = 0; i < a.length; ++i) {
         returnvalue[i].CopyConcat(a[i], c1, c2);
      }

      System.arraycopy(b, 0, returnvalue, a.length, b.length);
      return returnvalue;
   }

   private static StringPair[] doAlignments(String s1, String s2, int[][] d, int x, int y) {
      StringPair[] returnarray = new StringPair[0];
      if(x == 0 && y == 0) {
         returnarray = new StringPair[]{new StringPair()};
         return returnarray;
      } else {
         if(x > 0 && y > 0) {
            if(d[x][y] == d[x - 1][y - 1] && s1.charAt(x - 1) == s2.charAt(y - 1)) {
               returnarray = DoubleConcat(doAlignments(s1, s2, d, x - 1, y - 1), s1.charAt(x - 1), s2.charAt(y - 1), returnarray);
            }

            if(d[x][y] == d[x - 1][y - 1] + 1) {
               returnarray = DoubleConcat(doAlignments(s1, s2, d, x - 1, y - 1), s1.charAt(x - 1), s2.charAt(y - 1), returnarray);
            }
         }

         if(x > 0 && d[x][y] == d[x - 1][y] + 1) {
            returnarray = DoubleConcat(doAlignments(s1, s2, d, x - 1, y), s1.charAt(x - 1), '-', returnarray);
         }

         if(y > 0 && d[x][y] == d[x][y - 1] + 1) {
            returnarray = DoubleConcat(doAlignments(s1, s2, d, x, y - 1), '-', s2.charAt(y - 1), returnarray);
         }

         return returnarray;
      }
   }

   public StringPair[] getAlignments() {
      return doAlignments(this.s1, this.s2, this.d, this.s1.length(), this.s2.length());
   }

   public double meanAlignmentSize() {
      StringPair[] sp = this.getAlignments();
      double n = 0.0D;

      for(int i = 0; i < sp.length; ++i) {
         n += (double)sp[i].s1.length();
      }

      return n / (double)sp.length;
   }

   public double getErrorRateNew() {
      return (double)this.getMSD() / this.meanAlignmentSize() * 100.0D;
   }


   public  double totalErrorRate(String var0, String var1, double var2) {
      double var3 = (double) (Math.max(var0.length(), var1.length()) + var2);
      return (double) ((double)this.getMSD()+ var2) / var3;
   }

   public double uncorrErrorRate(String var0, String var1, double var2) {
      double var3 = (double) (Math.max(var0.length(), var1.length()) + var2);
      return (double)this.getMSD() / var3;
   }

   public double corrErrorRate(String var0, String var1, double var2) {
      double var3 = (double) (Math.max(var0.length(), var1.length()) + var2);
      return (double) var2 / var3;
   }


}
