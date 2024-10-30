package com.example.catlog;

import org.json.JSONObject;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SecretSharing {

    private static final BigInteger MODULUS = BigInteger.valueOf(1000000007); // Choose a large prime modulus

    public static void runSecretSharing(String filename) {
        try {
            // Step 1: Read JSON input from a specified file
            String content = new String(Files.readAllBytes(Paths.get(SecretSharing.class.getClassLoader().getResource(filename).toURI())));
            JSONObject jsonObject = new JSONObject(content); // Use JSONObject directly

            int n = jsonObject.getJSONObject("keys").getInt("n");
            int k = jsonObject.getJSONObject("keys").getInt("k");

            List<Integer> xValues = new ArrayList<>();
            List<BigInteger> yValues = new ArrayList<>();

            // Step 2: Decode Y values
            for (String key : jsonObject.keySet()) {
                if (!key.equals("keys")) {
                    JSONObject root = jsonObject.getJSONObject(key);
                    int base = root.getInt("base");
                    String value = root.getString("value");
                    BigInteger decodedValue = decodeValue(base, value);

                    xValues.add(Integer.parseInt(key));
                    yValues.add(decodedValue.mod(MODULUS)); // Use mod to ensure values are within bounds
                }
            }

            // Step 3: Calculate constant term c using Lagrange interpolation
            BigInteger c = calculateConstantTerm(xValues, yValues, k);

            // Step 4: Output the result
            System.out.println("The constant term c for file '" + filename + "' is: " + c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to decode value from a given base
    private static BigInteger decodeValue(int base, String value) {
        BigInteger decodedValue = BigInteger.ZERO;

        for (int i = value.length() - 1; i >= 0; i--) {
            char digit = value.charAt(i);
            int digitValue;

            // Determine if the digit is a letter (for bases greater than 10)
            if (Character.isDigit(digit)) {
                digitValue = digit - '0';
            } else {
                digitValue = Character.toLowerCase(digit) - 'a' + 10; // Convert 'a' to 'z'
            }

            decodedValue = decodedValue.add(BigInteger.valueOf(digitValue).multiply(BigInteger.valueOf(base).pow(value.length() - 1 - i)));
        }

        return decodedValue;
    }

    // Method to calculate the constant term c using Lagrange interpolation with modular arithmetic
    private static BigInteger calculateConstantTerm(List<Integer> xValues, List<BigInteger> yValues, int k) {
        BigInteger c = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            int xi = xValues.get(i);
            BigInteger yi = yValues.get(i);

            BigInteger li = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int xj = xValues.get(j);

                    BigInteger numerator = BigInteger.valueOf(-xj).mod(MODULUS);
                    BigInteger denominator = BigInteger.valueOf(xi - xj).mod(MODULUS);
                    BigInteger denominatorInverse = denominator.modInverse(MODULUS); // Use modular inverse for division

                    // Multiply li by (x - xj) / (xi - xj) in modular arithmetic
                    li = li.multiply(numerator).multiply(denominatorInverse).mod(MODULUS);
                }
            }

            // Add the term for yi * li to the constant c in modular arithmetic
            c = c.add(yi.multiply(li)).mod(MODULUS);
        }

        return c;
    }
}
