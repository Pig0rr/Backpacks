package com.spydnel.backpacks.networking;

import com.spydnel.backpacks.Backpacks;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BackpackOpenPayload(boolean isOpen, int id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BackpackOpenPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, "open_payload"));

    public static final StreamCodec<ByteBuf, BackpackOpenPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            BackpackOpenPayload::isOpen,
            ByteBufCodecs.INT,
            BackpackOpenPayload::id,
            BackpackOpenPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
