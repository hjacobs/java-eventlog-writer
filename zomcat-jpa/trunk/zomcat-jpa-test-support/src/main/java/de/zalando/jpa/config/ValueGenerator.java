package de.zalando.jpa.config;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

import org.joda.time.DateMidnight;

public class ValueGenerator {
    private static Random rnd = new Random();

    public static String generateRandomCode(final int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static Integer generateRandomInteger(final int length) {
        return rnd.nextInt(length);
    }

    public static BigDecimal generateRandomPercent() {
        return BigDecimal.valueOf(rnd.nextDouble() * 100);
    }

    public static Date generateDateByDays(final int deltaDays) {
        return DateMidnight.now().plusDays(deltaDays).toDate();
    }

    public static String generateModelSKu() {
        return generateRandomCode(9);
    }

    public static String generateConfigSku() {
        return generateModelSKu() + "-F" + generateRandomCode(2);
            // "12B53A003-F00"
            // return generateRandomCode(13);
    }

    public static String generateSimpleSku() {
        return generateConfigSku() + generateRandomCode(7);
    }

    public static String generateSimpleSkuByConfigSku(final String configSku) {
        return configSku + generateRandomCode(7);
    }

    public static String getConfigSkuBySimpleSku(final String simpleSku) {
        return simpleSku.substring(0, 13);
    }

    public static String generateRandomEmail() {
        final StringBuilder builder = new StringBuilder();
        builder.append(generateRandomCode(5)).append(".").append(generateRandomCode(5)).append("@")
               .append(generateRandomCode(4).toLowerCase()).append(".").append(generateRandomCode(2).toLowerCase());
        return builder.toString();
    }

    public static String generateItemGroupLevelTwo() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(generateRandomInteger(10)).append("-").append(generateRandomInteger(10));
        return stringBuilder.toString();
    }

    public static String getItemGroupLevelOne(final String itemGroupLevelTwo) {
        final String[] split = itemGroupLevelTwo.split("-");
        return split[0];
    }

}
