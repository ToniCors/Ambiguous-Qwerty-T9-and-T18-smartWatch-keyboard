package com.example.heisenberg.watchx;

public class StringPair {

   public String s1 = "";
   public String s2 = "";


   public void CopyConcat(StringPair p, char c1, char c2) {
      this.s1 = p.s1 + c1;
      this.s2 = p.s2 + c2;
   }
}
