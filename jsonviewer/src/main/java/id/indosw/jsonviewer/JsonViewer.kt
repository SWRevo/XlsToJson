@file:Suppress("PrivatePropertyName")

package id.indosw.jsonviewer

import android.animation.LayoutTransition
import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.widget.TextViewCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonViewer : LinearLayout {
    private val PADDING =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics)

    @ColorInt
    private var textColorString = 0

    @ColorInt
    private var textColorBool = 0

    @ColorInt
    private var textColorNull = 0

    @ColorInt
    private var textColorNumber = 0

    constructor(context: Context?) : super(context) {
        if (isInEditMode) initEditMode()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.let { init(context, it) }
        if (isInEditMode) initEditMode()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrs?.let { init(context, it) }
        if (isInEditMode) initEditMode()
    }

    @Suppress("DEPRECATION")
    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.JsonViewer, 0, 0
        )
        val r = resources
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                textColorString = a.getColor(
                    R.styleable.JsonViewer_textColorString,
                    context.getColor(R.color.jsonViewer_textColorString)
                )
                textColorNumber = a.getColor(
                    R.styleable.JsonViewer_textColorNumber,
                    context.getColor(R.color.jsonViewer_textColorNumber)
                )
                textColorBool = a.getColor(
                    R.styleable.JsonViewer_textColorBool,
                    context.getColor(R.color.jsonViewer_textColorBool)
                )
                textColorNull = a.getColor(
                    R.styleable.JsonViewer_textColorNull,
                    context.getColor(R.color.jsonViewer_textColorNull)
                )
            }
            else {
                textColorString = a.getColor(
                    R.styleable.JsonViewer_textColorString,
                    r.getColor(R.color.jsonViewer_textColorString)
                )
                textColorNumber = a.getColor(
                    R.styleable.JsonViewer_textColorNumber,
                    r.getColor(R.color.jsonViewer_textColorNumber)
                )
                textColorBool = a.getColor(
                    R.styleable.JsonViewer_textColorBool,
                    r.getColor(R.color.jsonViewer_textColorBool)
                )
                textColorNull = a.getColor(
                    R.styleable.JsonViewer_textColorNull,
                    r.getColor(R.color.jsonViewer_textColorNull)
                )
            }
        } finally {
            a.recycle()
        }
    }

    private fun initEditMode() {
        val json =
            "{\"id\":1,\"name\":\"Title\",\"is\":true,\"value\":null,\"array\":[{\"item\":1,\"name\":\"One\"},{\"item\":2,\"name\":\"Two\"}],\"object\":{\"id\":1,\"name\":\"Title\"},\"simple_array\":[1,2,3]}"
        try {
            setJson(JSONObject(json))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun setJson(json: Any?) {
        if (!(json is JSONArray || json is JSONObject)) throw RuntimeException("JsonViewer: JSON must be a instance of org.json.JSONArray or org.json.JSONObject")
        super.setOrientation(VERTICAL)
        removeAllViews()
        addJsonNode(this, null, json, false)
    }

    fun setTextColorString(@ColorInt color: Int) {
        textColorString = color
    }

    fun setTextColorNumber(@ColorInt color: Int) {
        textColorNumber = color
    }

    fun setTextColorBool(@ColorInt color: Int) {
        textColorBool = color
    }

    fun setTextColorNull(@ColorInt color: Int) {
        textColorNull = color
    }

    fun collapseJson() {
        var i = 0
        while (i < this.childCount) {
            if (getChildAt(i) is TextView &&
                getChildAt(i + 1) is ViewGroup &&
                getChildAt(i + 2) is TextView
            ) {
                changeVisibility(getChildAt(i + 1) as ViewGroup, VISIBLE)
                i += 2
            }
            i++
        }
    }

    fun expandJson() {
        changeVisibility(this, GONE)
    }

    private fun changeVisibility(group: ViewGroup, oldVisibility: Int) {
        var i = 0
        while (i < group.childCount) {
            if (group.getChildAt(i) is TextView &&
                group.getChildAt(i + 1) is ViewGroup &&
                group.getChildAt(i + 2) is TextView
            ) {
                val groupChild = group.getChildAt(i + 1) as ViewGroup
                groupChild.visibility = oldVisibility
                //groupChild.setLayoutTransition(null); // remove transition before mass change
                group.getChildAt(i).callOnClick()
                //groupChild.setLayoutTransition(new LayoutTransition());
                changeVisibility(group.getChildAt(i + 1) as ViewGroup, oldVisibility)
                i += 2
            }
            i++
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun addJsonNode(
        content: LinearLayout,
        nodeKey: Any?,
        jsonNode: Any,
        haveNext: Boolean
    ) {
        val haveChild = jsonNode is JSONObject && jsonNode.length() != 0 ||
                jsonNode is JSONArray && jsonNode.length() != 0
        val textViewHeader: TextView = getHeader(nodeKey, jsonNode, haveNext, true, haveChild)
        content.addView(textViewHeader)
        if (haveChild) {
            val viewGroupChild = getJsonNodeChild(nodeKey, jsonNode)
            val textViewFooter = getFooter(jsonNode, haveNext)
            content.addView(viewGroupChild)
            content.addView(textViewFooter)
            textViewHeader.setOnClickListener {
                if (viewGroupChild == null) return@setOnClickListener
                val newVisibility: Int
                val showChild: Boolean
                if (viewGroupChild.visibility == VISIBLE) {
                    newVisibility = GONE
                    showChild = false
                } else {
                    newVisibility = VISIBLE
                    showChild = true
                }
                textViewHeader.text =
                    getHeaderText(nodeKey, jsonNode, haveNext, showChild, haveChild)
                viewGroupChild.visibility = newVisibility
                if (textViewFooter != null) {
                    textViewFooter.visibility = newVisibility
                }
            }
        }
    }

    private fun getJsonNodeChild(nodeKey: Any?, jsonNode: Any): ViewGroup {
        val content = LinearLayout(context)
        content.orientation = VERTICAL
        content.setPadding(PADDING.toInt(), 0, 0, 0)
        if (nodeKey != null) {
            content.setBackgroundResource(R.drawable.background)
            //content.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }
        content.layoutTransition = LayoutTransition()
        if (jsonNode is JSONObject) {
            // setView key
            val iterator = jsonNode.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                // set view list
                try {
                    addJsonNode(content, key, jsonNode[key], iterator.hasNext())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        } else if (jsonNode is JSONArray) {
            // setView key
            for (i in 0 until jsonNode.length()) {
                // set view list
                try {
                    addJsonNode(content, i, jsonNode[i], i + 1 < jsonNode.length())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        return content
    }

    private fun getHeader(
        key: Any?,
        jsonNode: Any?,
        haveNext: Boolean,
        @Suppress("SameParameterValue")
        childDisplayed: Boolean,
        haveChild: Boolean
    ): TextView {
        val textView = TextView(context)
        textView.text = getHeaderText(key, jsonNode, haveNext, childDisplayed, haveChild)
        TextViewCompat.setTextAppearance(textView, R.style.JsonViewer_TextAppearance)
        textView.isFocusableInTouchMode = false
        textView.isFocusable = false
        return textView
    }

    private fun getHeaderText(
        key: Any?,
        jsonNode: Any?,
        haveNext: Boolean,
        childDisplayed: Boolean,
        haveChild: Boolean
    ): SpannableStringBuilder {
        val b = SpannableStringBuilder()
        if (key is String) {
            b.append("\"")
            b.append(key as String?)
            b.append("\"")
            b.append(": ")
        }
        if (!childDisplayed) {
            if (jsonNode is JSONArray) b.append("[ ... ]") else if (jsonNode is JSONObject) {
                b.append("{ ... }")
            }
            if (haveNext) b.append(",")
        } else {
            if (jsonNode is JSONArray) {
                b.append("[")
                if (!haveChild) b.append(getFooterText(jsonNode, haveNext))
            } else if (jsonNode is JSONObject) {
                b.append("{")
                if (!haveChild) b.append(getFooterText(jsonNode, haveNext))
            } else if (jsonNode != null) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (jsonNode is String) b.append(
                        "\"" + jsonNode + "\"",
                        ForegroundColorSpan(textColorString),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    ) else if (jsonNode is Int || jsonNode is Float || jsonNode is Double) b.append(
                        jsonNode.toString(),
                        ForegroundColorSpan(textColorNumber),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    ) else if (jsonNode is Boolean) b.append(
                        jsonNode.toString(),
                        ForegroundColorSpan(textColorBool),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    ) else if (jsonNode === JSONObject.NULL) b.append(
                        jsonNode.toString(),
                        ForegroundColorSpan(textColorNull),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    ) else b.append(jsonNode.toString())
                } else b.append(jsonNode.toString())
                if (haveNext) b.append(",")
                val span: LeadingMarginSpan = LeadingMarginSpan.Standard(0, PADDING.toInt())
                b.setSpan(span, 0, b.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return b
    }

    private fun getFooter(jsonNode: Any?, haveNext: Boolean): TextView {
        val textView = TextView(context)
        textView.text = getFooterText(jsonNode, haveNext)
        TextViewCompat.setTextAppearance(textView, R.style.JsonViewer_TextAppearance)
        textView.isFocusableInTouchMode = false
        textView.isFocusable = false
        return textView
    }

    private fun getFooterText(jsonNode: Any?, haveNext: Boolean): StringBuilder? {
        val builder = StringBuilder()
        when (jsonNode) {
            is JSONObject -> {
                builder.append("}")
            }
            is JSONArray -> {
                builder.append("]")
            }
            else -> return null
        }
        if (haveNext) builder.append(",")
        return builder
    }
}