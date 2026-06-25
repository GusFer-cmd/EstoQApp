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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
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
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    val showDialog = remember { mutableStateOf(false) }
    var clicked by remember { mutableStateOf(false) }

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
                        Text("Deletar", color = Color.Black)
                    }
                }
            }
        )
    }

    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ clicked = !clicked }
    ) {
        Row(
            modifier = Modifier
            .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(Color(storage.mainColor.toInt()))
            )
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier
                    .weight(1f)) {

                    Text(
                        text = storage.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Criado em: ${dateFormat.format(Date(storage.createdAt))}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.Blue)
                                .padding(15.dp,10.dp)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                            )
                        }

                        IconButton(
                            onClick = { showDialog.value = true },
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.Red)
                                .padding(15.dp,10.dp)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Lixeira",
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(storage.mainColor.toInt()))
                )
            }
        }
    }
}
