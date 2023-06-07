package com.example.eatit.ui

import android.util.Log
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
import com.example.eatit.model.Order
import com.example.eatit.model.Product
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.SectionCard
import com.example.eatit.viewModel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartViewModel: CartViewModel) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val order= cartViewModel.orderSelected!!
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
                        text = "€200.00",
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
                    onClick = { /*TODO*/ }
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
                    SectionSummaryCard(
                        sectionName = category.toString(),
                        products = products,
                        order = order
                    )
                }
        }
    }
}

@Composable
fun SectionSummaryCard(
    sectionName: String,
    products: List<Product>,
    order: Order
) {
    Text(
        modifier = Modifier.padding(20.dp, 10.dp),
        text = sectionName,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold
    )

    for (product in products) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.padding(5.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = CardDefaults.shape
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(17.dp, 7.dp),
                        text = product.name.toString(),
                        fontSize = 17.sp
                    )
                    Text(
                        modifier = Modifier.padding(17.dp, 7.dp),
                        text = product.price.toString()+"€ x "+order.listQuantity?.get(order.listProductId!!.indexOf(product.id!!)).toString(),
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}