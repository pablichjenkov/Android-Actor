package com.hamperapp.order


data class DryItemInfo(
	val cost: String,
	val image_path: String,
	val status: String,
	val title: String,
	val description_text: String,
	val type: String,
	val idObj: String,
	val sorting_number: Int,
	val count: Int
)

data class ItemSelectionResult(
	val selectedItems: List<DryItemInfo>?,
	val skipSelection: Boolean)

class CartResult(val keepPurchase: Boolean)
