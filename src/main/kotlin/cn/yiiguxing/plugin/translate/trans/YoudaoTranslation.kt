/*
 * YoudaoTranslation
 * 
 * Created by Yii.Guxing on 2017/10/30
 */
@file:Suppress("ArrayInDataClass", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")

package cn.yiiguxing.plugin.translate.trans

import cn.yiiguxing.plugin.translate.message
import cn.yiiguxing.plugin.translate.trans.text.NamedTranslationDocument
import cn.yiiguxing.plugin.translate.trans.text.YoudaoDictDocument
import cn.yiiguxing.plugin.translate.trans.text.YoudaoWebTranslationDocument
import com.google.gson.annotations.SerializedName


data class YoudaoTranslation(
    @SerializedName("query")
    var query: String? = null,
    @SerializedName("errorCode")
    var errorCode: Int = -1,
    var message: String? = null,
    @SerializedName("translation")
    var translation: Array<out String>? = null,
    @SerializedName("basic")
    var basicExplain: YBasicExplain? = null,
    @SerializedName("l")
    var languages: String? = null,
    @SerializedName("web")
    var webExplains: Array<out YWebExplain>? = null
) : TranslationAdapter {

    val isSuccessful get() = errorCode == 0

    fun checkError() {
        if (errorCode == 0 && translation?.isEmpty() != false && basicExplain?.explains?.isEmpty() != false) {
            errorCode = 302
        }
    }

    override fun toTranslation(): Translation {
        check(isSuccessful) { "Can not convert to Translation: errorCode=$errorCode" }
        check(query != null) { "Can not convert to Translation: query=$query" }
        check(!languages.isNullOrBlank()) { "Can not convert to Translation: languages=$languages" }

        val languagesList = languages!!.split("2")
        check(languagesList.size == 2) { "Can not convert to Translation: languages=$languages" }

        val srcLang = Lang.valueOfYoudaoCode(languagesList[0])
        val transLang = Lang.valueOfYoudaoCode(languagesList[1])

        val phonetic = basicExplain?.let { basicExplain ->
            val phoneticUK = basicExplain.phoneticUK?.let { "[UK] $it" } ?: ""
            val phoneticUS = basicExplain.phoneticUS?.let { "[US] $it" } ?: ""
            "$phoneticUK    $phoneticUS".takeIf { it.isNotBlank() }
        }

        return Translation(
            query!!,
            translation?.firstOrNull(),
            srcLang,
            transLang,
            listOf(srcLang),
            phonetic,
            dictDocument = YoudaoDictDocument.Factory.getDocument(this),
            extraDocument = YoudaoWebTranslationDocument.Factory.getDocument(this)
                ?.let { NamedTranslationDocument(message("tip.label.webInterpretation"), it) }
        )
    }
}

data class YBasicExplain(
    @SerializedName(value = "phonetic")
    var phonetic: String? = null,
    @SerializedName(value = "uk-phonetic")
    var phoneticUK: String? = null,
    @SerializedName(value = "us-phonetic")
    var phoneticUS: String? = null,
    @SerializedName(value = "explains")
    var explains: Array<out String>? = null,
    @SerializedName(value = "wfs")
    var wordForms: Array<out YWordFormWrapper>? = null
)

data class YWebExplain(
    @SerializedName(value = "key")
    var key: String? = null,
    @SerializedName(value = "value")
    var values: Array<out String>? = null
)

data class YWordFormWrapper(@SerializedName(value = "wf") val wordForm: YWordForm)
data class YWordForm(
    @SerializedName(value = "name")
    val name: String,
    @SerializedName(value = "value")
    val value: String
)