package haishu.crawler

trait Item
case class MapItem[T](m: Map[String, T]) extends Item
case class ProductItem[T <: Product](p: T) extends Item
