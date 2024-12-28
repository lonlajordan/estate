package com.estate.domain.helper;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class TextUtils {
    public static String generatePassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private static long get64LeastSignificantBitsForVersion1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong | variant3BitFlag;
    }

    private static long get64MostSignificantBitsForVersion1() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long time_low = (currentTimeMillis & 0x0000_0000_FFFF_FFFFL) << 32;
        final long time_mid = ((currentTimeMillis >> 32) & 0xFFFF) << 16;
        final long version = 1 << 12;
        final long time_hi = ((currentTimeMillis >> 48) & 0x0FFF);
        return time_low | time_mid | version | time_hi;
    }

    public static UUID generateType1UUID() {
        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();
        return new UUID(most64SigBits, least64SigBits);
    }

    public static String generatePassword(Integer length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        while (password.length() < length) { // length of the random string.
            int index = (int) (random.nextFloat() * characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }
}
