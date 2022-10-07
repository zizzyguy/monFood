package kr.co.dreameut.monthfood

import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

/**
 * AES256 암호화/복호화
 */
object SeedCipher {

    private val iv = "mysdis2002_iv".toByteArray(StandardCharsets.UTF_8)
    private val key = "mysdis2002_key".toByteArray(StandardCharsets.UTF_8)

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
    fun encryptString(str: String): String {
        val enc = KISA_SEED_CBC.SEED_CBC_Encrypt(key, iv, str.toByteArray(StandardCharsets.UTF_8), 0, str.toByteArray().size)
        return String(enc)
    }
}