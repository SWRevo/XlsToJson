@file:Suppress("SpellCheckingInspection")

package id.indosw.fileutil

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import java.net.URLDecoder

object FileUtil {
    @JvmStatic
    fun convertUriToFilePath(context: Context, uri: Uri): String? {
        var path: String? = null
        when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        path = if ("primary".equals(type, ignoreCase = true)) {
                            //path = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                            "/mnt/sdcard" + "/" + split[1]
                        }else {
                            "/mnt/sdcard" + "/" + Environment.DIRECTORY_DOCUMENTS + "/" + split[1]
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        val id = DocumentsContract.getDocumentId(uri)
                        if (!TextUtils.isEmpty(id)) {
                            if (id.startsWith("raw:")) {
                                return id.replaceFirst("raw:".toRegex(), "")
                            }
                        }
                        val contentUri = ContentUris
                            .withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
                        path = getDataColumn(context, contentUri, null, null)
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            "video" -> {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }
                            "audio" -> {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }
                        }
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(
                            split[1]
                        )
                        path = getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                }
            }
            ContentResolver.SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true) -> {
                path = getDataColumn(context, uri, null, null)
            }
            ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true) -> {
                path = uri.path
            }
        }
        return if (path != null) {
            try {
                URLDecoder.decode(path, "UTF-8")
            } catch (e: Exception) {
                null
            }
        } else null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        //val column = MediaStore.Images.Media.DATA
        val column = MediaStore.Images.Media._ID
        val projection = arrayOf(
            column
        )
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } catch (ignored: Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}