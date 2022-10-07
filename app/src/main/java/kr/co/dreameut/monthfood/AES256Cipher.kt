package kr.co.dreameut.monthfood

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES256 암호화/복호화
 */
object AES256Cipher {

    private val ivBytes = byteArrayOf(
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00
    )
    private const val aes256_key = "WE0dpkC3tbACQLEXfBBWrcqmH0AYdyNX" //암호화 복호화 키

    /**
     * 암호화
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun AES_Encode(str: String): String {
        val textBytes = str.toByteArray(StandardCharsets.UTF_8)
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(
            ivBytes
        )
        val newKey = SecretKeySpec(aes256_key.toByteArray(StandardCharsets.UTF_8), "AES")
        val cipher= Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec)
        return Base64.encodeToString(cipher.doFinal(textBytes), 0)
    }

    /**
     * 복호화
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        UnsupportedEncodingException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun AES_Decode(str: String?): String {
        val textBytes = Base64.decode(str, 0)
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(
            ivBytes
        )
        val newKey = SecretKeySpec(aes256_key.toByteArray(charset("UTF-8")), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec)
        return String(cipher.doFinal(textBytes))
    }
}