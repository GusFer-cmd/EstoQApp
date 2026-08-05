package com.example.estoq.component

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeCard(
    cardIcon: ImageVector,
    cardTotalNumber: Long,
    cardTittle: String,
    cardBackground: Color
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(contentColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBackground)
                .padding(10.dp, 14.dp),
            verticalArrangement = Arrangement.spacedBy(19.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Icon(
                imageVector = cardIcon,
                contentDescription = cardTittle,
                modifier = Modifier
                    .size(25.dp)
            )

            Box {
                Column (
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = cardTotalNumber.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )

                    Text(
                        text = cardTittle,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}