"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ArrowLeft } from "lucide-react"

interface CartItem {
  id: number
  name: string
  price: number
  quantity: number
}

export default function CheckoutPage() {
  const router = useRouter()
  const [cartItems, setCartItems] = useState<CartItem[]>([])
  const [formData, setFormData] = useState({
    email: "",
    firstName: "",
    lastName: "",
    address: "",
    city: "",
    zipCode: "",
    phone: "",
    paymentMethod: "",
  })

  useEffect(() => {
    const savedCart = localStorage.getItem("cart")
    if (savedCart) {
      const cart = JSON.parse(savedCart)
      const itemCounts = cart.reduce((acc: any, item: any) => {
        acc[item.id] = (acc[item.id] || 0) + 1
        return acc
      }, {})

      const uniqueItems = cart.filter(
        (item: any, index: number, self: any[]) => index === self.findIndex((t) => t.id === item.id),
      )

      const cartWithQuantity = uniqueItems.map((item: any) => ({
        ...item,
        quantity: itemCounts[item.id],
      }))

      setCartItems(cartWithQuantity)
    }
  }, [])

  const getTotalPrice = () => {
    return cartItems.reduce((total, item) => total + item.price * item.quantity, 0)
  }

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()

    // Validate form
    const requiredFields = ["email", "firstName", "lastName", "address", "city", "zipCode", "phone", "paymentMethod"]
    const missingFields = requiredFields.filter((field) => !formData[field as keyof typeof formData])

    if (missingFields.length > 0) {
      alert("Vui lòng điền đầy đủ thông tin")
      return
    }

    // Create order
    const order = {
      id: Date.now(),
      items: cartItems,
      total: getTotalPrice(),
      customerInfo: formData,
      orderDate: new Date().toISOString(),
      status: "confirmed",
    }

    // Save order and clear cart
    localStorage.setItem("lastOrder", JSON.stringify(order))
    localStorage.removeItem("cart")

    // Redirect to confirmation
    router.push("/order-confirmation")
  }

  if (cartItems.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Giỏ hàng trống</h1>
          <Link href="/">
            <Button>Quay về trang chủ</Button>
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center h-16">
            <Link href="/cart">
              <Button variant="ghost" data-testid="back-button">
                <ArrowLeft className="h-4 w-4 mr-2" />
                Quay lại giỏ hàng
              </Button>
            </Link>
            <h1 className="text-2xl font-bold ml-4" data-testid="checkout-title">
              Thanh toán
            </h1>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="space-y-6">
              <Card>
                <CardHeader>
                  <CardTitle>Thông tin liên hệ</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <Label htmlFor="email">Email *</Label>
                    <Input
                      id="email"
                      type="email"
                      value={formData.email}
                      onChange={(e) => handleInputChange("email", e.target.value)}
                      data-testid="email-input"
                      required
                    />
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Địa chỉ giao hàng</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="firstName">Họ *</Label>
                      <Input
                        id="firstName"
                        value={formData.firstName}
                        onChange={(e) => handleInputChange("firstName", e.target.value)}
                        data-testid="first-name-input"
                        required
                      />
                    </div>
                    <div>
                      <Label htmlFor="lastName">Tên *</Label>
                      <Input
                        id="lastName"
                        value={formData.lastName}
                        onChange={(e) => handleInputChange("lastName", e.target.value)}
                        data-testid="last-name-input"
                        required
                      />
                    </div>
                  </div>
                  <div>
                    <Label htmlFor="address">Địa chỉ *</Label>
                    <Input
                      id="address"
                      value={formData.address}
                      onChange={(e) => handleInputChange("address", e.target.value)}
                      data-testid="address-input"
                      required
                    />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="city">Thành phố *</Label>
                      <Input
                        id="city"
                        value={formData.city}
                        onChange={(e) => handleInputChange("city", e.target.value)}
                        data-testid="city-input"
                        required
                      />
                    </div>
                    <div>
                      <Label htmlFor="zipCode">Mã bưu điện *</Label>
                      <Input
                        id="zipCode"
                        value={formData.zipCode}
                        onChange={(e) => handleInputChange("zipCode", e.target.value)}
                        data-testid="zip-code-input"
                        required
                      />
                    </div>
                  </div>
                  <div>
                    <Label htmlFor="phone">Số điện thoại *</Label>
                    <Input
                      id="phone"
                      type="tel"
                      value={formData.phone}
                      onChange={(e) => handleInputChange("phone", e.target.value)}
                      data-testid="phone-input"
                      required
                    />
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Phương thức thanh toán</CardTitle>
                </CardHeader>
                <CardContent>
                  <Select
                    value={formData.paymentMethod}
                    onValueChange={(value) => handleInputChange("paymentMethod", value)}
                  >
                    <SelectTrigger data-testid="payment-method-select">
                      <SelectValue placeholder="Chọn phương thức thanh toán" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="credit-card">Thẻ tín dụng</SelectItem>
                      <SelectItem value="debit-card">Thẻ ghi nợ</SelectItem>
                      <SelectItem value="paypal">PayPal</SelectItem>
                      <SelectItem value="bank-transfer">Chuyển khoản ngân hàng</SelectItem>
                      <SelectItem value="cod">Thanh toán khi nhận hàng</SelectItem>
                    </SelectContent>
                  </Select>
                </CardContent>
              </Card>
            </div>

            <div>
              <Card data-testid="order-summary">
                <CardHeader>
                  <CardTitle>Đơn hàng của bạn</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-3">
                    {cartItems.map((item) => (
                      <div key={item.id} className="flex justify-between items-center">
                        <div>
                          <span className="font-medium">{item.name}</span>
                          <span className="text-gray-600 ml-2">x{item.quantity}</span>
                        </div>
                        <span>${item.price * item.quantity}</span>
                      </div>
                    ))}
                  </div>

                  <div className="border-t pt-4 space-y-2">
                    <div className="flex justify-between">
                      <span>Tạm tính:</span>
                      <span>${getTotalPrice()}</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Phí vận chuyển:</span>
                      <span>Miễn phí</span>
                    </div>
                    <div className="flex justify-between text-lg font-bold border-t pt-2">
                      <span>Tổng cộng:</span>
                      <span data-testid="total-price">${getTotalPrice()}</span>
                    </div>
                  </div>

                  <Button type="submit" className="w-full mt-6" data-testid="place-order-button">
                    Đặt hàng
                  </Button>
                </CardContent>
              </Card>
            </div>
          </div>
        </form>
      </main>
    </div>
  )
}
