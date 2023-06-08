package com.example.eatit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R
import com.example.eatit.model.Product
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.SectionShoppingCard
import com.example.eatit.viewModel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartViewModel: CartViewModel) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val order = cartViewModel.orderSelected
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 115.dp,
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        modifier = Modifier.padding(20.dp, 0.dp),
                        text = "Totale:",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.padding(20.dp, 0.dp),
                        text = order.totalPrice.toString() + "€",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Divider(modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 0.dp))
                Text(
                    modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp),
                    text = "Orario di ritiro:",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = "14:30",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    modifier = Modifier.padding(20.dp),
                    onClick = { cartViewModel.addNewOrder(cartViewModel.orderSelected) }
                ) {
                    Text(
                        modifier = Modifier.padding(20.dp, 10.dp),
                        text = "PAGA",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        BackgroundImage(alpha = 0.05f)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 10.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = CardDefaults.shape
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 5.dp),
                    text = "Riepilogo ordine",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            var products by remember { mutableStateOf<List<Product>>(emptyList()) }
            LaunchedEffect(Unit) {
                products = cartViewModel.getProducts(order)
            }
            LocalContext.current.resources.getStringArray(R.array.categories)
                .forEach { category ->
                    SectionShoppingCard(
                        sectionName = category.toString(),
                        products = products,
                        order = order,
                    )
                }
        }
    }
}

