package se.zensum.storage.Codec

import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.codec.ToByteBufEncoder
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset

class StringToByteArrayCodec constructor(
    private val charset: Charset = Charset.defaultCharset(),
    private var utf8: Boolean = true,
    private var ascii: Boolean = false
) : RedisCodec<String, ByteArray>, ToByteBufEncoder<String, ByteArray> {
    private val EMPTY = ByteArray(0)

    override fun encodeKey(key: String?): ByteBuffer {
        return encodeStringAndAllocateBuffer(key)
    }

    override fun encodeKey(key: String?, target: ByteBuf) {
        encodeString(key, target)
    }

    override fun decodeKey(bytes: ByteBuffer): String = Unpooled.wrappedBuffer(bytes).toString(charset)

    override fun encodeValue(value: ByteArray?): ByteBuffer {
        return if (value == null) {
            ByteBuffer.wrap(EMPTY)
        } else ByteBuffer.wrap(value)

    }

    override fun encodeValue(value: ByteArray?, target: ByteBuf?) {
        if (value != null) {
            target?.writeBytes(value)
        }
    }

    override fun decodeValue(bytes: ByteBuffer): ByteArray {
        return getBytes(bytes)

    }

    override fun estimateSize(keyOrValue: Any?): Int {
        if (keyOrValue is String) {
            val encoder = CharsetUtil.encoder(charset)
            return (encoder.averageBytesPerChar() * keyOrValue.length).toInt()
        }
        return 0
    }

    private fun encodeString(str: String?, target: ByteBuf) {
        if (str == null) return

        if (utf8) {
            ByteBufUtil.writeUtf8(target, str)
            return
        }

        if (ascii) {
            ByteBufUtil.writeAscii(target, str)
            return
        }

        val encoder = CharsetUtil.encoder(charset)
        val length = (str.length.toDouble() * encoder.maxBytesPerChar()).toInt()
        target.ensureWritable(length)
        try {
            val dstBuf = target.nioBuffer(0, length)
            val pos = dstBuf.position()
            var cr = encoder.encode(CharBuffer.wrap(str), dstBuf, true)
            if (!cr.isUnderflow) {
                cr.throwException()
            }
            cr = encoder.flush(dstBuf)
            if (!cr.isUnderflow) {
                cr.throwException()
            }
            target.writerIndex(target.writerIndex() + dstBuf.position() - pos)
        } catch (x: CharacterCodingException) {
            throw IllegalStateException(x)
        }
    }

    /**
     * Compatibility implementation.
     *
     * @param key
     * @return
     */
    private fun encodeStringAndAllocateBuffer(key: String?): ByteBuffer {
        if (key == null) return ByteBuffer.wrap(EMPTY)

        val encoder = CharsetUtil.encoder(charset)
        val buffer = ByteBuffer.allocate((encoder.maxBytesPerChar() * key.length).toInt())

        val byteBuf = Unpooled.wrappedBuffer(buffer)
        byteBuf.clear()
        encodeString(key, byteBuf)
        buffer.limit(byteBuf.writerIndex())

        return buffer
    }

    private fun getBytes(buffer: ByteBuffer): ByteArray {
        val remaining = buffer.remaining()
        if (remaining == 0) return EMPTY

        val b = ByteArray(remaining)
        buffer.get(b)
        return b
    }
}