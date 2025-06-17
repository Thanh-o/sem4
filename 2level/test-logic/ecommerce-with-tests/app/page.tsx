"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { ShoppingCart } from "lucide-react"

interface Product {
  id: number
  name: string
  price: number
  image: string
  category: string
  description: string
}

const products: Product[] = [
  {
    id: 1,
    name: "iPhone 15 Pro",
    price: 999,
    image: "/placeholder.svg?height=200&width=200",
    category: "Điện thoại",
    description: "iPhone 15 Pro với chip A17 Pro mạnh mẽ",
  },
  {
    id: 2,
    name: "MacBook Air M2",
    price: 1199,
    image: "/placeholder.svg?height=200&width=200",
    category: "Laptop",
    description: "MacBook Air với chip M2 siêu mỏng nhẹ",
  },
  {
    id: 3,
    name: "AirPods Pro",
    price: 249,
    image: "/placeholder.svg?height=200&width=200",
    category: "Phụ kiện",
    description: "AirPods Pro với chống ồn chủ động",
  },
  {
    id: 4,
    name: "iPad Pro",
    price: 799,
    image: "/placeholder.svg?height=200&width=200",
    category: "Tablet",
    description: "iPad Pro với màn hình Liquid Retina",
  },
]

export default function HomePage() {
  const [cart, setCart] = useState<Product[]>([])

  useEffect(() => {
    const savedCart = localStorage.getItem("cart")
    if (savedCart) {
      setCart(JSON.parse(savedCart))
    }
  }, [])

  const addToCart = (product: Product) => {
    const newCart = [...cart, product]
    setCart(newCart)
    localStorage.setItem("cart", JSON.stringify(newCart))
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-2xl font-bold text-gray-900" data-testid="site-title">
              TechStore
            </h1>
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
        <div className="mb-8">
          <h2 className="text-3xl font-bold text-gray-900 mb-2">Sản phẩm nổi bật</h2>
          <p className="text-gray-600">Khám phá những sản phẩm công nghệ mới nhất</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6" data-testid="products-grid">
          {products.map((product) => (
            <Card key={product.id} className="hover:shadow-lg transition-shadow" data-testid={`product-${product.id}`}>
              <CardHeader>
                <img
                  src={product.image || "/placeholder.svg"}
                  alt={product.name}
                  className="w-full h-48 object-cover rounded-md"
                />
              </CardHeader>
              <CardContent>
                <CardTitle className="text-lg mb-2">{product.name}</CardTitle>
                <Badge variant="secondary" className="mb-2">
                  {product.category}
                </Badge>
                <p className="text-gray-600 text-sm mb-3">{product.description}</p>
                <p className="text-2xl font-bold text-blue-600">${product.price}</p>
              </CardContent>
              <CardFooter className="flex gap-2">
                <Link href={`/product/${product.id}`} className="flex-1">
                  <Button variant="outline" className="w-full" data-testid={`view-product-${product.id}`}>
                    Xem chi tiết
                  </Button>
                </Link>
                <Button onClick={() => addToCart(product)} className="flex-1" data-testid={`add-to-cart-${product.id}`}>
                  Thêm vào giỏ
                </Button>
              </CardFooter>
            </Card>
          ))}
        </div>
      </main>
    </div>
  )
}
