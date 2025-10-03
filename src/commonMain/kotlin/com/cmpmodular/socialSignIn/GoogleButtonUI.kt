//package com.cmpmodular.socialSignIn
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import kmp_app_template.composeapp.generated.resources.Res
//import kmp_app_template.composeapp.generated.resources.ic_apple
//import kmp_app_template.composeapp.generated.resources.ic_google
//import org.jetbrains.compose.resources.painterResource
//
//@Composable
//internal fun GoogleButtonUI(
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Image(
//        modifier = Modifier.then(modifier)
//            .size(60.dp)
//            .clip(CircleShape)
//            .clickable {
//                onClick.invoke()
//            }
//            .background(Color.White)
//            .padding(16.dp),
//        painter = painterResource(Res.drawable.ic_google),
//        contentDescription = null
//    )
//}
//
//@Composable
//internal fun AppleButtonUI(
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Image(
//        modifier = Modifier.then(modifier)
//            .size(60.dp)
//            .clip(CircleShape)
//            .clickable {
//                onClick.invoke()
//            }
//            .background(Color.White)
//            .padding(16.dp),
//        painter = painterResource(Res.drawable.ic_apple),
//        contentDescription = null
//    )
//}