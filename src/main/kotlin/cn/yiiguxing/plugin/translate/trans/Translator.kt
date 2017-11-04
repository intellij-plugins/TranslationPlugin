package cn.yiiguxing.plugin.translate.trans

/**
 * Translator
 *
 * Created by Yii.Guxing on 2017-10-29 0029.
 */
interface Translator {

    val id: String

    val supportedSourceLanguages: List<Lang>
    
    val supportedTargetLanguages: List<Lang>

    fun translate(text: String, srcLang: Lang, targetLang: Lang): Translation

}