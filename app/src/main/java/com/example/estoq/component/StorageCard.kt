package com.example.estoq.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estoq.R
import com.example.estoq.data.Model.Storage.Storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StorageCard(
    storage: Storage,
    onNavigateToItemStorage: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            shape = RoundedCornerShape(20.dp),

            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color(0xFFFFF3E0),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "!",
                            color = Color.Red,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tem certeza?",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },

            text = {
                Text(
                    text = "Ao selecionar 'Deletar' o estoque será apagado.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            },

            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { showDialog.value = false },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onDeleteClick()
                            showDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Deletar", color = Color.White)
                    }
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ onNavigateToItemStorage() },
        colors = CardDefaults.cardColors(
            containerColor = Color(storage.mainColor.toInt())
        ),
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row( modifier = Modifier
                .padding(top = 14.dp, bottom = 20.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box (
                            modifier = Modifier
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color.Black)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Estoque ${storage.id}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Caixa de estoque",
                            modifier = Modifier.size(28.dp),
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = storage.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Clique no card correspondente para visualizar as informações do estoque, incluindo os respectivos itens e a quantidade total.",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            StorageButtons(
                                color = Color(0xFF4382DF),
                                onAction = { onEditClick() },
                                icon = Icons.Default.Edit,
                                description = "Editar"
                            )

                            Spacer(Modifier.width(10.dp))

                            StorageButtons(
                                color = Color(0xFFDC0000),
                                onAction = { showDialog.value = true },
                                icon = Icons.Default.DeleteForever,
                                description = "Excluir"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StorageButtons(
    color: Color,
    onAction: () -> Unit,
    icon: ImageVector,
    description: String
) {
    IconButton(
        onClick = { onAction() },
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(color)
            .padding(25.dp,5.dp)
            .size(20.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description
        )
    }
}
