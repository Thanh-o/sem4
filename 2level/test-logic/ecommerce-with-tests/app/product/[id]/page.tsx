"use client"

import { useState, useEffect } from "react"
import { useParams, useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ArrowLeft, ShoppingCart, Star } from "lucide-react"
import Link from "next/link"

interface Product {
  id: number
  name: string
  price: number
  image: string
  category: string
  description: string
  features: string[]
  rating: number
  reviews: number
}

const products: Product[] = [
  {
    id: 1,
    name: "iPhone 15 Pro",
    price: 999,
    image: "/placeholder.svg?height=400&width=400",
    category: "Điện thoại",
    description: "iPhone 15 Pro với chip A17 Pro mạnh mẽ, camera chuyên nghiệp và thiết kế titanium cao cấp.",
    features: ["Chip A17 Pro", "Camera 48MP", "Màn hình ProMotion", "Thiết kế Titanium"],
    rating: 4.8,
    reviews: 1250,
  },
  {
    id: 2,
    name: "MacBook Air M2",
    price: 1199,
    image: "/placeholder.svg?height=400&width=400",
    category: "Laptop",
    description: "MacBook Air với chip M2 siêu mỏng nhẹ, hiệu năng vượt trội và thời lượng pin cả ngày.",
    features: ["Chip M2", "13.6 inch Liquid Retina", "18 giờ pin", "Siêu mỏng nhẹ"],
    rating: 4.9,
    reviews: 890,
  },
  {
    id: 3,
    name: "AirPods Pro",
    price: 249,
    image: "/placeholder.svg?height=400&width=400",
    category: "Phụ kiện",
    description: "AirPods Pro với chống ồn chủ động, âm thanh không gian và thiết kế thoải mái.",
    features: ["Chống ồn chủ động", "Âm thanh không gian", "6 giờ nghe nhạc", "Chống nước IPX4"],
    rating: 4.7,
    reviews: 2100,
  },
  {
    id: 4,
    name: "iPad Pro",
    price: 799,
    image: "/placeholder.svg?height=400&width=400",
    category: "Tablet",
    description: "iPad Pro với màn hình Liquid Retina, chip M2 và hỗ trợ Apple Pencil thế hệ 2.",
    features: ["Chip M2", "11 inch Liquid Retina", "Hỗ trợ Apple Pencil", "5G tùy chọn"],
    rating: 4.6,
    reviews: 750,
  },
]

export default function ProductPage() {
  const params = useParams()
  const router = useRouter()
  const [cart, setCart] = useState<Product[]>([])
  const [quantity, setQuantity] = useState(1)

  const productId = Number.parseInt(params.id as string)
  const product = products.find((p) => p.id === productId)

  useEffect(() => {
    const savedCart = localStorage.getItem("cart")
    if (savedCart) {
      setCart(JSON.parse(savedCart))
    }
  }, [])

  if (!product) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Không tìm thấy sản phẩm</h1>
          <Link href="/">
            <Button>Quay về trang chủ</Button>
          </Link>
        </div>
      </div>
    )
  }

  const addToCart = () => {
    const newItems = Array(quantity).fill(product)
    const newCart = [...cart, ...newItems]
    setCart(newCart)
    localStorage.setItem("cart", JSON.stringify(newCart))
    router.push("/cart")
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <Link href="/">
              <Button variant="ghost" data-testid="back-button">
                <ArrowLeft className="h-4 w-4 mr-2" />
                Quay lại
              </Button>
            </Link>
            <Link href="/cart">
              <Button variant="outline" className="relative" data-testid="cart-button">
                <ShoppingCart className="h-5 w-5" />
                {cart.length > 0 && (
                  <Badge className="absolute -top-2 -right-2 h-5 w-5 rounded-full p-0 flex items-center justify-center">
                    {cart.length}
                  </Badge>
                )}
              </Button>
            </Link>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div>
            <img
              src={product.image || "/placeholder.svg"}
              alt={product.name}
              className="w-full h-96 object-cover rounded-lg"
              data-testid="product-image"
            />
          </div>

          <div>
            <Badge variant="secondary" className="mb-4">
              {product.category}
            </Badge>
            <h1 className="text-3xl font-bold text-gray-900 mb-4" data-testid="product-name">
              {product.name}
            </h1>

            <div className="flex items-center mb-4">
              <div className="flex items-center">
                {[...Array(5)].map((_, i) => (
                  <Star
                    key={i}
                    className={`h-5 w-5 ${i < Math.floor(product.rating) ? "text-yellow-400 fill-current" : "text-gray-300"}`}
                  />
                ))}
              </div>
              <span className="ml-2 text-gray-600">
                {product.rating} ({product.reviews} đánh giá)
              </span>
            </div>

            <p className="text-4xl font-bold text-blue-600 mb-6" data-testid="product-price">
              ${product.price}
            </p>

            <p className="text-gray-700 mb-6" data-testid="product-description">
              {product.description}
            </p>

            <Card className="mb-6">
              <CardHeader>
                <CardTitle>Tính năng nổi bật</CardTitle>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2">
                  {product.features.map((feature, index) => (
                    <li key={index} className="flex items-center">
                      <span className="w-2 h-2 bg-blue-600 rounded-full mr-3"></span>
                      {feature}
                    </li>
                  ))}
                </ul>
              </CardContent>
            </Card>

            <div className="flex items-center gap-4 mb-6">
              <label htmlFor="quantity" className="font-medium">
                Số lượng:
              </label>
              <select
                id="quantity"
                value={quantity}
                onChange={(e) => setQuantity(Number.parseInt(e.target.value))}
                className="border rounded-md px-3 py-2"
                data-testid="quantity-select"
              >
                {[1, 2, 3, 4, 5].map((num) => (
                  <option key={num} value={num}>
                    {num}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex gap-4">
              <Button onClick={addToCart} className="flex-1" data-testid="add-to-cart-button">
                <ShoppingCart className="h-5 w-5 mr-2" />
                Thêm vào giỏ hàng
              </Button>
              <Button variant="outline" className="flex-1">
                Mua ngay
              </Button>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}
