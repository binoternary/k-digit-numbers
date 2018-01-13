# k-digit numbers

> A nine-digit number is formed using each of the digits 1, 2, 3, ..., 9 exactly once.
  For n = 1, 2, 3, ..., 9, n divides the first n digits of the number. Find the number.
-- [1]

Generalizing that to arbitrary bases
a k-digit number in base k+1 is formed using each of the digits 1, 2, 3, ..., k exactly once.
For n = 1, 2, 3, ..., k, n divides the first n digits of the number.


This program looks for those numbers.
This was inspired by the book "Things to Make and Do in the Fourth Dimension" by Matt Parker [2]


## Running the program

Java 8 is required.  
Build the project with `mvn clean package`.  
Run with `java -jar kdigit-*.jar [start [end]]`
where `start` and `end` are optional parameters to specify the range of
bases where to look for k-digit numbers.

E.g `java -jar kdigit-*.jar 10 20` will look through bases 10 to 20 (inclusive).  
`java -jar kdigit-*.jar 10` will look from base 20 up to base 2^31 - 1.  
`java -jar kdigit-*.jar` will look from base 2 up to base 2^31 - 1

A usable jar can be downloaded from the [releases section](https://github.com/binoternary/k-digit-numbers/releases).

## Solutions fo far

The solutions have a prefix to indicate the base followed by the digits.
Each digit is written as its decimal value and digits are separated by underscore.

```
base-2 : [b2-1]
base-4 : [b4-1_2_3, b4-3_2_1]
base-6 : [b6-1_4_3_2_5, b6-5_4_3_2_1]
base-8 : [b8-3_2_5_4_1_6_7, b8-5_2_3_4_7_6_1, b8-5_6_7_4_3_2_1]
base-10 : [b10-3_8_1_6_5_4_7_2_9]
base-14 : [b14-9_12_3_10_5_4_7_6_11_8_1_2_13]
```

Other bases up to (and including) base-30 don't contain any solutions.

[1] http://jwilson.coe.uga.edu/emt725/Class/Lanier/Nine.Digit/nine.html  
[2] https://www.amazon.co.uk/Things-Make-Do-Fourth-Dimension/dp/0141975865/
