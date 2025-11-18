package com.example.kasapp.ui.view.kasir

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.R
import com.example.kasapp.ui.viewmodel.Kasir.CartItem
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private enum class DragAnchors {
    Start,
    End
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RincianPesananView(
    viewModel: KasirViewModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val isPaymentSelected = uiState.selectedPayment.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rincian Pesanan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(4.dp)
                            .clickable { onBackClick() },
                        contentScale = ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // List items
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    items(
                        items = uiState.cart,
                        key = { it.menu.idMenu }
                    ) { cartItem ->
                        SwipeableCartItem(
                            cartItem = cartItem,
                            onDelete = { viewModel.removeItemFromCart(cartItem) },
                            onTambah = { viewModel.addItemToCart(cartItem.menu) },
                            onKurang = { viewModel.decreaseItemInCart(cartItem) }
                        )
                    }
                }

                // Divider sebelum section payment
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Color(0x43000000)
                )

                // Bottom section dengan background kuning
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFB300))
                ) {
                    Column {
                        // White section dengan rounded corner untuk payment
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(bottomStart = 33.dp, bottomEnd = 33.dp)
                                )
                                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Jenis Pembayaran",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Spacer(modifier = Modifier.height(27.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(55.dp, Alignment.CenterHorizontally)
                                ) {
                                    PaymentButton(
                                        text = "Qris",
                                        iconRes = R.drawable.qris,
                                        selected = uiState.selectedPayment == "QRIS",
                                        onClick = { viewModel.selectPaymentMethod("QRIS") },
                                        color = Color(0xFFD6E3FF),
                                        textColor = Color(0xFF4F8FFD),
                                        modifier = Modifier.width(130.dp)
                                    )
                                    PaymentButton(
                                        text = "Tunai",
                                        iconRes = R.drawable.tunai,
                                        selected = uiState.selectedPayment == "Tunai",
                                        onClick = { viewModel.selectPaymentMethod("Tunai") },
                                        color = Color(0xFFD6FFD7),
                                        textColor = Color(0xFF32C05B),
                                        modifier = Modifier.width(130.dp)
                                    )
                                }
                            }
                        }

                        // Bottom bar untuk total dan tombol bayar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFB300))
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total Item : ${uiState.totalCartItems}",
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = formatRupiah(uiState.totalCartPrice),
                                    fontSize = 37.sp,
                                    fontWeight = FontWeight.W900,
                                    color = Color.White
                                )
                            }
                            Button(
                                onClick = {
                                    if (isPaymentSelected) {
                                        viewModel.saveTransaction {
                                            scope.launch(Dispatchers.Main) {
                                                onSaveClick()
                                            }
                                        }
                                    }
                                },
                                enabled = isPaymentSelected,
                                modifier = Modifier
                                    .height(48.dp)
                                    .widthIn(min = 100.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    disabledContainerColor = Color(0xFFFFD54F)
                                ),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                Text(
                                    text = "Bayar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPaymentSelected) Color(0xFF32C05B) else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeableCartItem(
    cartItem: CartItem,
    onDelete: () -> Unit,
    onTambah: () -> Unit,
    onKurang: () -> Unit
) {
    val density = LocalDensity.current
    val swipeThreshold = 65.dp
    val swipeThresholdPx = with(density) { swipeThreshold.toPx() }
    val state = remember(swipeThresholdPx) {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 0.4f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(durationMillis = 300),
            decayAnimationSpec = exponentialDecay()
        ).apply {
            updateAnchors(
                DraggableAnchors {
                    DragAnchors.Start at 0f
                    DragAnchors.End at -swipeThresholdPx
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = Color(0xFFFFD6D6),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(end = 6.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sampah),
                    contentDescription = "Hapus",
                    modifier = Modifier.size(33.dp)
                )
            }
        }

        RincianItemCard(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = state.offset.roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(state, Orientation.Horizontal),
            item = cartItem,
            onTambah = onTambah,
            onKurang = onKurang
        )
    }
}

@Composable
private fun RincianItemCard(
    item: CartItem,
    onTambah: () -> Unit,
    onKurang: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.menu.namaMenu,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = item.menu.jenisMenu,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatRupiah(item.menu.hargaMenu),
                    fontSize = 16.sp,
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold
                )
            }

            QuantityControl(
                itemCount = item.quantity,
                onTambah = onTambah,
                onKurang = onKurang
            )
        }
    }
}

@Composable
private fun PaymentButton(
    text: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Color(0xFFFF9800) else Color.Transparent

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = color,
        border = BorderStroke(3.dp, borderColor),
        modifier = modifier.height(55.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun QuantityControl(
    itemCount: Int,
    onTambah: () -> Unit,
    onKurang: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .background(
                    Color(0xFFFFF0F0),
                    RoundedCornerShape(12.dp)
                )
                .clickable { onKurang() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                fontSize = 22.sp,
                color = Color.Red
            )
        }
        Text(
            text = itemCount.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .size(35.dp)
                .background(
                    Color(0xFFFFB300),
                    RoundedCornerShape(12.dp)
                )
                .clickable { onTambah() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 22.sp,
                color = Color.White
            )
        }
    }
}

