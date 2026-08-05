package com.example.estoq.notification.manager

import android.app.Notification
import androidx.core.app.NotificationCompat
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.notification.channel.NotificationChannelManager.Companion.CHANNEL_INSIGHTS
import com.example.estoq.notification.channel.NotificationChannelManager.Companion.CHANNEL_LOW_STOCK
import com.example.estoq.notification.channel.NotificationChannelManager.Companion.CHANNEL_REMINDERS
import com.example.estoq.notification.channel.NotificationChannelManager.Companion.CHANNEL_SALES_OPERATIONS
import com.example.estoq.notification.model.NotificationEvent
import com.example.estoq.notification.model.NotificationGroups
import com.example.estoq.notification.model.NotificationIds
import java.text.NumberFormat
import java.util.Locale

private val CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

class NotificationFactory(
    private val helper: NotificationHelper
) {

    fun create(event: NotificationEvent, context: android.content.Context): Pair<Int, Notification> = when (event) {
        is NotificationEvent.LowStock -> createLowStock(context, event.items)
        is NotificationEvent.ZeroStock -> createZeroStock(context, event.item)
        is NotificationEvent.SaleCreated -> createSaleCreated(context, event)
        is NotificationEvent.StockRestored -> createStockRestored(context, event.itemNames)
        is NotificationEvent.WeeklySummary -> createWeeklySummary(context, event)
        is NotificationEvent.RestockSuggestion -> createRestockSuggestion(context, event.items)
        is NotificationEvent.UnsoldItems -> createUnsoldItems(context, event.items, event.days)
        is NotificationEvent.InventoryReminder -> createInventoryReminder(context)
        is NotificationEvent.SaleDeleted -> createSaleDeleted(context, event)
        is NotificationEvent.SalesSummary -> createSalesSummary(context, event)
    }

    fun createSummary(
        groupKey: String,
        channelId: String,
        title: String,
        text: String,
        context: android.content.Context
    ): Pair<Int, Notification> {
        val summaryId = when (groupKey) {
            NotificationGroups.LOW_STOCK -> NotificationIds.LOW_STOCK_SUMMARY
            NotificationGroups.SALES -> NotificationIds.SALE_SUMMARY
            NotificationGroups.INSIGHTS -> NotificationIds.INSIGHTS_SUMMARY
            else -> 999
        }
        return summaryId to helper.buildSummary(
            channelId = channelId,
            groupKey = groupKey,
            title = title,
            text = text,
            intent = PendingIntentFactory.home(context)
        )
    }

    private fun createLowStock(context: android.content.Context, items: List<Item>): Pair<Int, Notification> {
        if (items.size > 1) {
            val inbox = NotificationCompat.InboxStyle()
            inbox.setBigContentTitle("${items.size} itens com estoque baixo")
            for (item in items) {
                inbox.addLine("${item.name}: ${item.stockQuantity} un.")
            }
            val id = NotificationIds.LOW_STOCK_SUMMARY
            return id to helper.build(
                channelId = CHANNEL_LOW_STOCK,
                title = "${items.size} itens com estoque baixo",
                text = "${zeroCount(items)} zerados, ${lowCount(items)} abaixo do mínimo",
                style = inbox,
                groupKey = NotificationGroups.LOW_STOCK,
                intent = PendingIntentFactory.itemIndex(context),
                priority = NotificationCompat.PRIORITY_HIGH,
                smallIcon = android.R.drawable.ic_dialog_alert
            )
        }

        val item = items.first()
        val id = (NotificationIds.LOW_STOCK_BASE + item.id).toInt()
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("Item: ${item.name}\nMarca: ${item.brand}\nQuantidade: ${item.stockQuantity}\nPreço: ${CURRENCY_FORMAT.format(item.currentPrice)}")
        return id to helper.build(
            channelId = CHANNEL_LOW_STOCK,
            title = "Estoque baixo: ${item.name}",
            text = "Apenas ${item.stockQuantity} unidade(s) restante(s)",
            style = bigText,
            groupKey = NotificationGroups.LOW_STOCK,
            intent = PendingIntentFactory.itemUpdate(context, item.id, id),
            actions = listOf(
                NotificationCompat.Action(
                    android.R.drawable.ic_menu_edit, "Repor",
                    PendingIntentFactory.itemUpdate(context, item.id, id + 10000)
                )
            ),
            priority = NotificationCompat.PRIORITY_HIGH,
            smallIcon = android.R.drawable.ic_dialog_alert
        )
    }

    private fun createZeroStock(context: android.content.Context, item: Item): Pair<Int, Notification> {
        val id = (NotificationIds.LOW_STOCK_BASE + item.id).toInt()
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("⚠️ ITEM SEM ESTOQUE\n\nItem: ${item.name}\nMarca: ${item.brand}\nPreço: ${CURRENCY_FORMAT.format(item.currentPrice)}\n\nAdicione reposição o quanto antes.")
        return id to helper.build(
            channelId = CHANNEL_LOW_STOCK,
            title = "Estoque ZERADO: ${item.name}",
            text = "Este item está sem unidades disponíveis",
            style = bigText,
            groupKey = NotificationGroups.LOW_STOCK,
            intent = PendingIntentFactory.itemUpdate(context, item.id, id),
            actions = listOf(
                NotificationCompat.Action(
                    android.R.drawable.ic_menu_edit, "Reposição Urgente",
                    PendingIntentFactory.itemUpdate(context, item.id, id + 10000)
                )
            ),
            priority = NotificationCompat.PRIORITY_HIGH,
            smallIcon = android.R.drawable.ic_dialog_alert
        )
    }

    private fun createSaleCreated(context: android.content.Context, event: NotificationEvent.SaleCreated): Pair<Int, Notification> {
        val id = (NotificationIds.SALE_BASE + event.saleId).toInt()
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("Cliente: ${event.clientName}\nValor: ${CURRENCY_FORMAT.format(event.total)}\nItens: ${event.itemCount}\nPagamento: registrado")
        return id to helper.build(
            channelId = CHANNEL_SALES_OPERATIONS,
            title = "Venda registrada",
            text = "${event.clientName} — ${CURRENCY_FORMAT.format(event.total)}",
            style = bigText,
            groupKey = NotificationGroups.SALES,
            intent = PendingIntentFactory.saleDetail(context, event.saleId, id),
            actions = listOf(
                NotificationCompat.Action(
                    android.R.drawable.ic_menu_view, "Ver detalhes",
                    PendingIntentFactory.saleDetail(context, event.saleId, id + 10000)
                )
            ),
            priority = NotificationCompat.PRIORITY_LOW
        )
    }

    private fun createSaleDeleted(context: android.content.Context, event: NotificationEvent.SaleDeleted): Pair<Int, Notification> {
        val id = (NotificationIds.SALE_BASE + event.saleId).toInt()
        val formattedTotal = CURRENCY_FORMAT.format(event.total)
        return id to helper.build(
            channelId = CHANNEL_SALES_OPERATIONS,
            title = "Venda deletada",
            text = "$formattedTotal — ${event.clientName}",
            groupKey = NotificationGroups.SALES,
            intent = PendingIntentFactory.saleDetail(context, event.saleId, id),
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    private fun createStockRestored(context: android.content.Context, itemNames: List<String>): Pair<Int, Notification> {
        val id = (NotificationIds.SALE_BASE + 99999).toInt()
        val inbox = NotificationCompat.InboxStyle()
        inbox.setBigContentTitle("Itens restaurados")
        for (name in itemNames) {
            inbox.addLine("✓ $name")
        }
        return id to helper.build(
            channelId = CHANNEL_SALES_OPERATIONS,
            title = "Estoque restaurado",
            text = "${itemNames.size} item(ns) voltaram ao estoque",
            style = inbox,
            groupKey = NotificationGroups.SALES,
            intent = PendingIntentFactory.itemIndex(context),
            priority = NotificationCompat.PRIORITY_LOW
        )
    }

    private fun createWeeklySummary(context: android.content.Context, event: NotificationEvent.WeeklySummary): Pair<Int, Notification> {
        val id = NotificationIds.LOW_STOCK_SUMMARY + 1
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("📊 Resumo do Estoque\n\nItens com estoque baixo: ${event.lowStockCount}\nItens zerados: ${event.zeroStockCount}\n\nClique para ver todos os itens.")
        return id to helper.build(
            channelId = CHANNEL_LOW_STOCK,
            title = "Resumo semanal do estoque",
            text = "${event.lowStockCount} itens com estoque baixo, ${event.zeroStockCount} zerados",
            style = bigText,
            groupKey = NotificationGroups.LOW_STOCK,
            intent = PendingIntentFactory.itemIndex(context),
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    private fun createRestockSuggestion(context: android.content.Context, items: List<Item>): Pair<Int, Notification> {
        val id = NotificationIds.RESTOCK_SUGGESTION
        val inbox = NotificationCompat.InboxStyle()
        inbox.setBigContentTitle("Sugestões de reposição")
        for (item in items) {
            inbox.addLine("${item.name} (apenas ${item.stockQuantity} un.)")
        }
        return id to helper.build(
            channelId = CHANNEL_INSIGHTS,
            title = "Sugestões de reposição",
            text = "${items.size} itens podem precisar de reposição",
            style = inbox,
            groupKey = NotificationGroups.INSIGHTS,
            intent = PendingIntentFactory.itemIndex(context),
            priority = NotificationCompat.PRIORITY_LOW
        )
    }

    private fun createUnsoldItems(context: android.content.Context, items: List<Item>, days: Int): Pair<Int, Notification> {
        val id = NotificationIds.UNSOLD_ITEMS
        val inbox = NotificationCompat.InboxStyle()
        inbox.setBigContentTitle("Itens sem saída")
        for (item in items) {
            inbox.addLine("${item.name} — sem vendas há $days dias")
        }
        return id to helper.build(
            channelId = CHANNEL_INSIGHTS,
            title = "Itens sem saída",
            text = "${items.size} itens não foram vendidos nos últimos $days dias",
            style = inbox,
            groupKey = NotificationGroups.INSIGHTS,
            intent = PendingIntentFactory.itemIndex(context),
            priority = NotificationCompat.PRIORITY_LOW
        )
    }

    private fun createSalesSummary(context: android.content.Context, event: NotificationEvent.SalesSummary): Pair<Int, Notification> {
        val id = NotificationIds.SALES_MONTHLY_SUMMARY
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("📈 Vendas de ${event.monthLabel}\n\nVendas: ${event.count}\nTotal: ${CURRENCY_FORMAT.format(event.totalValue)}\n\nClique para ver todos os registros.")
        return id to helper.build(
            channelId = CHANNEL_SALES_OPERATIONS,
            title = "Resumo mensal de vendas",
            text = "${event.count} vendas — ${CURRENCY_FORMAT.format(event.totalValue)}",
            style = bigText,
            groupKey = NotificationGroups.SALES,
            intent = PendingIntentFactory.home(context),
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    private fun createInventoryReminder(context: android.content.Context): Pair<Int, Notification> {
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("Uma revisão periódica do estoque ajuda a evitar divergências.\n\nClique para ver todos os estoques cadastrados.")
        return NotificationIds.INVENTORY_REMINDER to helper.build(
            channelId = CHANNEL_REMINDERS,
            title = "Hora de revisar o estoque",
            text = "Faça uma contagem física para manter a acuracidade do inventário",
            style = bigText,
            groupKey = null,
            intent = PendingIntentFactory.storageIndex(context),
            actions = listOf(
                NotificationCompat.Action(
                    android.R.drawable.ic_menu_today, "Adiar 1 hora",
                    PendingIntentFactory.home(context)
                )
            ),
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    private fun zeroCount(items: List<Item>) = items.count { it.stockQuantity == 0 }
    private fun lowCount(items: List<Item>) = items.count { it.stockQuantity in 1..5 }
}
