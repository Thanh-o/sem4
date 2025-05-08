package com.mycompany.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Inventory getInventorySample1() {
        return new Inventory().id(1L).quantity(1).warehouse("warehouse1").productId(1L);
    }

    public static Inventory getInventorySample2() {
        return new Inventory().id(2L).quantity(2).warehouse("warehouse2").productId(2L);
    }

    public static Inventory getInventoryRandomSampleGenerator() {
        return new Inventory()
            .id(longCount.incrementAndGet())
            .quantity(intCount.incrementAndGet())
            .warehouse(UUID.randomUUID().toString())
            .productId(longCount.incrementAndGet());
    }
}
