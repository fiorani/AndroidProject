package com.example.eatit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryScreen() {
    val scaffoldState = rememberBottomSheetScaffoldState()

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
            SectionSummaryCard("Dessert", listOf("Asticee Bollyuto", "Funghi & Tartufy"))
            SectionSummaryCard("Dolcini", listOf("Astice Bollito", "Funghi & Tartufy","Astice Bollito", "Funghi & Tartufy"))
            SectionSummaryCard("Bevande", listOf("Astice Bollito", "Funghi & Tartufy"))
            SectionSummaryCard("Antipasti", listOf("Astice Bollito", "Funghi & Tartufy", "Funghi & Tartufy"))
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SectionSummaryCard(
sectionName: String,
products: List<String>
) {
    Text(
        modifier = Modifier.padding(20.dp, 10.dp),
        text = sectionName,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold
    )

    for(product in products) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.padding(5.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = CardDefaults.shape
            ){
                Row(
                    modifier = Modifier.width(285.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(17.dp,7.dp),
                        text = product,
                        fontSize = 17.sp
                    )
                    Text(
                        modifier = Modifier.padding(17.dp, 7.dp),
                        text = "€40.01",
                        fontSize = 17.sp
                    )
                }
            }
            Text(
                modifier = Modifier.padding(17.dp,0.dp),
                text = "x 0",
                fontSize = 17.sp
            )
        }
    }
}