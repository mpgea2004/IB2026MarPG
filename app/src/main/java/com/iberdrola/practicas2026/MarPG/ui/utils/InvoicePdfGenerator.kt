package com.iberdrola.practicas2026.MarPG.ui.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import java.io.OutputStream

object InvoicePdfGenerator {

    fun generateAndSaveInvoicePdf(context: Context, invoice: Invoice): Uri? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        canvas.drawColor(Color.WHITE)

        val colorGreen = Color.parseColor("#148f32")
        val colorBlue = Color.parseColor("#004d8c")
        val colorOrange = Color.parseColor("#fbb034")

        paint.style = Paint.Style.FILL
        
        paint.color = colorGreen
        canvas.drawRect(0f, 0f, 198f, 8f, paint)
        paint.color = colorBlue
        canvas.drawRect(198f, 0f, 396f, 8f, paint)
        paint.color = colorOrange
        canvas.drawRect(396f, 0f, 595f, 8f, paint)

        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.iberdrola)
            if (logoBitmap != null) {
                val ratio = logoBitmap.height.toFloat() / logoBitmap.width.toFloat()
                val targetWidth = 140f
                val targetHeight = targetWidth * ratio
                val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, targetWidth.toInt(), targetHeight.toInt(), true)
                canvas.drawBitmap(scaledLogo, 40f, 35f, null)
            }
        } catch (e: Exception) {
            paint.color = colorGreen
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("IBERDROLA", 40f, 60f, paint)
        }

        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("FACTURA DE SERVICIOS", 415f, 60f, paint)
        
        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.DKGRAY
        canvas.drawText(if (invoice.contractType == ContractType.LUZ) "Suministro Eléctrico" else "Suministro de Gas", 415f, 75f, paint)

        paint.color = Color.BLACK
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Resumen de la factura", 40f, 160f, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("ID de Factura:", 40f, 200f, paint)
        paint.isFakeBoldText = true
        canvas.drawText(invoice.id, 160f, 200f, paint)

        paint.isFakeBoldText = false
        canvas.drawText("Fecha de emisión:", 40f, 225f, paint)
        canvas.drawText(invoice.issueDate, 160f, 225f, paint)

        canvas.drawText("Periodo facturado:", 40f, 250f, paint)
        canvas.drawText("${invoice.startDate} al ${invoice.endDate}", 160f, 250f, paint)

        canvas.drawText("Estado actual:", 40f, 275f, paint)
        paint.color = if (invoice.status.description == "Pagadas") colorGreen else Color.RED
        canvas.drawText(invoice.status.description, 160f, 275f, paint)

        paint.color = colorGreen
        canvas.drawRect(40f, 310f, 211f, 312f, paint)
        paint.color = colorBlue
        canvas.drawRect(211f, 310f, 382f, 312f, paint)
        paint.color = colorOrange
        canvas.drawRect(382f, 310f, 555f, 312f, paint)

        paint.color = Color.parseColor("#F4F4F4")
        canvas.drawRect(40f, 340f, 555f, 400f, paint)
        
        paint.color = Color.BLACK
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("TOTAL FACTURA", 60f, 375f, paint)
        
        paint.textSize = 22f
        paint.color = colorGreen
        val amountStr = String.format("%.2f €", invoice.amount)
        canvas.drawText(amountStr, 440f, 375f, paint)

        // 8. Pie
        paint.color = Color.GRAY
        paint.textSize = 9f
        paint.isFakeBoldText = false
        canvas.drawText("Iberdrola Clientes S.A.U. - Este documento es una representación gráfica de su factura.", 40f, 780f, paint)
        canvas.drawText("Gracias por contribuir al medio ambiente con la factura digital.", 40f, 795f, paint)

        pdfDocument.finishPage(page)

        return savePdfToDownloads(context, pdfDocument, "Factura_${invoice.id}.pdf")
    }

    private fun savePdfToDownloads(context: Context, pdfDocument: PdfDocument, fileName: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        return try {
            uri?.let {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.use { os ->
                    pdfDocument.writeTo(os)
                }
                pdfDocument.close()
                it 
            }
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
