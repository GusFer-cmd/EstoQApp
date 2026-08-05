package com.example.estoq.screen.MainGraph.Analytic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estoq.component.Container
import com.example.estoq.component.MonthlyProfitBarChart
import com.example.estoq.data.Model.PivotSaleItem.MonthlyProfitSummary
import com.example.estoq.data.Viewmodel.Analytic.AnalyticViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyProfitScreen(
    analyticViewModel: AnalyticViewModel,
    onNavigateBack: () -> Unit
) {

    val monthlyProfits by analyticViewModel.monthlyProfits.collectAsState(initial = emptyList())

    var selectedIndex by remember { mutableStateOf(0) }

    val allMonths = remember(monthlyProfits) {
        val earliest = monthlyProfits.minOfOrNull { it.monthKey } ?: return@remember emptyList()
        monthKeysBetween(earliest, currentMonthKey())
            .map { key ->
                monthlyProfits.firstOrNull { it.monthKey == key }
                    ?: MonthlyProfitSummary(key, 0.0, 0, 0)
            }
            .sortedByDescending { it.monthKey }
    }

    val safeSelectedIndex = if (allMonths.isEmpty()) {
        0
    } else {
        selectedIndex.coerceIn(0, allMonths.lastIndex)
    }
    val selectedMonth = allMonths.getOrNull(safeSelectedIndex)

    val selectedKey = selectedMonth?.monthKey

    val chartWindow = remember(allMonths, selectedKey) {
        allMonths.sortedBy { it.monthKey }
            .filter { selectedKey == null || it.monthKey <= selectedKey }
            .takeLast(5)
    }

    val selectedInWindow = selectedKey?.let { key ->
        chartWindow.indexOfFirst { it.monthKey == key }.takeIf { it >= 0 }
    }

    val totalProfit = remember(monthlyProfits) { monthlyProfits.sumOf { it.profit } }
    val totalItemsSold = remember(monthlyProfits) { monthlyProfits.sumOf { it.totalQuantity.toLong() }.toInt() }
    val totalSales = remember(monthlyProfits) { monthlyProfits.sumOf { it.totalSales.toLong() }.toInt() }

    val priceFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lucro Mensal",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            if (monthlyProfits.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sem vendas registradas",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Container {
                    LazyColumn(
                        modifier = Modifier.padding(bottom = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column {
                                Spacer(Modifier.height(16.dp))

                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF355872),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AttachMoney,
                                            contentDescription = "Lucro",
                                            tint = Color.White,
                                            modifier = Modifier.padding(bottom = 12.dp)
                                        )

                                        Text(
                                            text = "Lucro total",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )

                                        Spacer(Modifier.height(6.dp))

                                        Text(
                                            text = priceFormat.format(totalProfit),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 28.sp
                                        )
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.ShoppingBag,
                                        label = "Itens vendidos",
                                        value = totalItemsSold.toString()
                                    )

                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.ReceiptLong,
                                        label = "Vendas",
                                        value = totalSales.toString()
                                    )
                                }
                            }
                        }

                        if (monthlyProfits.isNotEmpty()) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Lucro por Mês",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )

                                        Spacer(Modifier.height(16.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = { selectedIndex++ },
                                                enabled = safeSelectedIndex < allMonths.lastIndex
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                                    contentDescription = "Mês anterior"
                                                )
                                            }

                                            Column(
                                                modifier = Modifier.weight(1f),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                if (selectedMonth != null) {
                                                    Text(
                                                        text = formatMonthName(selectedMonth.monthKey),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )

                                                    Text(
                                                        text = "${selectedMonth.totalQuantity} itens · ${selectedMonth.totalSales} vendas",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { selectedIndex-- },
                                                enabled = safeSelectedIndex > 0
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                    contentDescription = "Mês seguinte"
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        MonthlyProfitBarChart(
                                            data = chartWindow,
                                            selectedIndex = selectedInWindow,
                                            onBarClick = { index ->
                                                chartWindow.getOrNull(index)?.monthKey?.let { key ->
                                                    selectedIndex = allMonths.indexOfFirst { it.monthKey == key }
                                                }
                                            }
                                        )

                                        if (selectedMonth != null) {
                                            Spacer(Modifier.height(16.dp))

                                            HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.4f))

                                            Spacer(Modifier.height(12.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = formatMonthName(selectedMonth.monthKey),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )

                                                Text(
                                                    text = priceFormat.format(selectedMonth.profit),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = if (selectedMonth.profit >= 0) {
                                                        Color(0xFF1B7A3D)
                                                    } else {
                                                        Color.Red
                                                    }
                                                )
                                            }

                                            Spacer(Modifier.height(4.dp))

                                            Text(
                                                text = "${selectedMonth.totalQuantity} itens vendidos · ${selectedMonth.totalSales} vendas",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Column {
                                Text(
                                    text = "Lucro por Mês",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Spacer(Modifier.height(8.dp))

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        allMonths.forEachIndexed { index, month ->
                                            MonthProfitRow(
                                                monthKey = month.monthKey,
                                                profit = month.profit,
                                                totalQuantity = month.totalQuantity,
                                                totalSales = month.totalSales,
                                                priceFormat = priceFormat,
                                                isSelected = month.monthKey == selectedKey
                                            )

                                            if (index < allMonths.lastIndex) {
                                                HorizontalDivider(
                                                    thickness = 1.dp,
                                                    color = Color.Gray.copy(alpha = 0.2f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.size(12.dp))

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MonthProfitRow(
    monthKey: String,
    profit: Double,
    totalQuantity: Int,
    totalSales: Int,
    priceFormat: NumberFormat,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = formatMonthName(monthKey),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Unspecified
                }
            )

            Text(
                text = "$totalQuantity itens · $totalSales vendas",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = priceFormat.format(profit),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (profit >= 0) Color(0xFF1B7A3D) else Color.Red
        )
    }
}

private fun formatMonthName(monthKey: String): String {
    return try {
        val date = SimpleDateFormat("yyyy-MM", Locale.US).parse(monthKey)
        val name = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")).format(date)
        name.replaceFirstChar { it.uppercase() }
    } catch (e: Exception) {
        monthKey
    }
}

private fun currentMonthKey(): String = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

private fun monthKeysBetween(startKey: String, endKey: String): List<String> {
    val keys = mutableListOf<String>()
    val format = SimpleDateFormat("yyyy-MM", Locale.US)
    val calendar = Calendar.getInstance()
    val startParts = startKey.split('-')
    calendar.clear()
    calendar.set(Calendar.YEAR, startParts[0].toInt())
    calendar.set(Calendar.MONTH, startParts[1].toInt() - 1)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    var key = format.format(calendar.time)
    while (key <= endKey) {
        keys.add(key)
        calendar.add(Calendar.MONTH, 1)
        key = format.format(calendar.time)
    }
    return keys
}
