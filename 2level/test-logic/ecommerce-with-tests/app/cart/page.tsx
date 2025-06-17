"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ArrowLeft, Minus, Plus, Trash2 } from "lucide-react"

interface Product {
  id: number
  name: string
  price: number
  image: string
  category: string
  description: string
}

interface CartItem extends Product {
  quantity: number
}

export default function CartPage() {
  const [cartItems, setCartItems] = useState<CartItem[]>([])

  useEffect(() => {
    const savedCart = localStorage.getItem("cart")
    if (savedCart) {
      const cart: Product[] = JSON.parse(savedCart)
      const itemCounts = cart.reduce(
        (acc, item) => {
          acc[item.id] = (acc[item.id] || 0) + 1
          return acc
        },
        {} as Record<number, number>,
      )

      const uniqueItems = cart.filter((item, index, self) => index === self.findIndex((t) => t.id === item.id))

      const cartWithQuantity = uniqueItems.map((item) => ({
        ...item,
        quantity: itemCounts[item.id],
      }))

      setCartItems(cartWithQuantity)
    }
  }, [])

  const updateCart = (newCartItems: CartItem[]) => {
    setCartItems(newCartItems)
    const flatCart = newCartItems.flatMap((item) =>
      Array(item.quantity).fill({
        id: item.id,
        name: item.name,
        price: item.price,
        image: item.image,
        category: item.category,
        description: item.description,
      }),
    )
    localStorage.setItem("cart", JSON.stringify(flatCart))
  }

  const updateQuantity = (id: number, newQuantity: number) => {
    if (newQuantity === 0) {
      removeItem(id)
      return
    }

    const updatedItems = cartItems.map((item) => (item.id === id ? { ...item, quantity: newQuantity } : item))
    updateCart(updatedItems)
  }

  const removeItem = (id: number) => {
    const updatedItems = cartItems.filter((item) => item.id !== id)
    updateCart(updatedItems)
  }

  const getTotalPrice = () => {
    return cartItems.reduce((total, item) => total + item.price * item.quantity, 0)
  }

  const getTotalItems = () => {
    return cartItems.reduce((total, item) => total + item.quantity, 0)
  }

  if (cartItems.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50">
        <header className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex items-center h-16">
              <Link href="/">
                <Button variant="ghost" data-testid="back-button">
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Quay lại
                </Button>
              </Link>
            </div>
          </div>
        </header>

        <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="text-center" data-testid="empty-cart">
            <h1 className="text-2xl font-bold mb-4">Giỏ hàng trống</h1>
            <p className="text-gray-600 mb-6">Bạn chưa có sản phẩm nào trong giỏ hàng</p>
            <Link href="/">
              <Button>Tiếp tục mua sắm</Button>
            </Link>
          </div>
        </main>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center h-16">
            <Link href="/">
              <Button variant="ghost" data-testid="back-button">
                <ArrowLeft className="h-4 w-4 mr-2" />
                Quay lại
              </Button>
            </Link>
            <h1 className="text-2xl font-bold ml-4" data-testid="cart-title">
              Giỏ hàng ({getTotalItems()} sản phẩm)
            </h1>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <div className="space-y-4" data-testid="cart-items">
              {cartItems.map((item) => (
                <Card key={item.id} data-testid={`cart-item-${item.id}`}>
                  <CardContent className="p-6">
                    <div className="flex items-center gap-4">
                      <img
                        src={item.image || "/placeholder.svg"}
                        alt={item.name}
                        className="w-20 h-20 object-cover rounded-md"
                      />
                      <div className="flex-1">
                        <h3 className="font-semibold text-lg">{item.name}</h3>
                        <p className="text-gray-600">{item.category}</p>
                        <p className="text-blue-600 font-bold text-xl">${item.price}</p>
                      </div>
                      <div className="flex items-center gap-3">
                        <Button
                          variant="outline"
                          size="icon"
                          onClick={() => updateQuantity(item.id, item.quantity - 1)}
                          data-testid={`decrease-${item.id}`}
                        >
                          <Minus className="h-4 w-4" />
                        </Button>
                        <span className="w-8 text-center font-medium" data-testid={`quantity-${item.id}`}>
                          {item.quantity}
                        </span>
                        <Button
                          variant="outline"
                          size="icon"
                          onClick={() => updateQuantity(item.id, item.quantity + 1)}
                          data-testid={`increase-${item.id}`}
                        >
                          <Plus className="h-4 w-4" />
                        </Button>
                      </div>
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => removeItem(item.id)}
                        className="text-red-600 hover:text-red-700"
                        data-testid={`remove-${item.id}`}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>

          <div>
            <Card data-testid="order-summary">
              <CardHeader>
                <CardTitle>Tóm tắt đơn hàng</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex justify-between">
                  <span>Tạm tính:</span>
                  <span data-testid="subtotal">${getTotalPrice()}</span>
                </div>
                <div className="flex justify-between">
                  <span>Phí vận chuyển:</span>
                  <span>Miễn phí</span>
                </div>
                <div className="border-t pt-4">
                  <div className="flex justify-between text-lg font-bold">
                    <span>Tổng cộng:</span>
                    <span data-testid="total-price">${getTotalPrice()}</span>
                  </div>
                </div>
                <Link href="/checkout" className="block">
                  <Button className="w-full" data-testid="checkout-button">
                    Tiến hành thanh toán
                  </Button>
                </Link>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  )
}
