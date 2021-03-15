package me.raulsmartin.breeze.types;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum BreezeType implements IStringSerializable {
    TYPE_1("type1"),
    TYPE_2("type2"),
    TYPE_3("type3"),
    TYPE_4("type4"),
    TYPE_5("type5");

    private final String name;

    BreezeType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Nonnull
    public String getSerializedName() {
        return this.name;
    }
}