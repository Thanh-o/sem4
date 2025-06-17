"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { CheckCircle, Package, Truck, Home } from "lucide-react"

interface Order {
  id: number
  items: Array<{
    id: number
    name: string
    price: number
    quantity: number
  }>
  total: number
  customerInfo: {
    email: string
    firstName: string
    lastName: string
    address: string
    city: string
    zipCode: string
    phone: string
    paymentMethod: string
  }
  orderDate: string
  status: string
}

export default function OrderConfirmationPage() {
  const [order, setOrder] = useState<Order | null>(null)

  useEffect(() => {
    const savedOrder = localStorage.getItem("lastOrder")
    if (savedOrder) {
      setOrder(JSON.parse(savedOrder))
    }
  }, [])

  if (!order) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Không tìm thấy đơn hàng</h1>
          <Link href="/">
            <Button>Quay về trang chủ</Button>
          </Link>
        </div>
      </div>
    )
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("vi-VN", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    })
  }

  const getPaymentMethodText = (method: string) => {
    const methods: Record<string, string> = {
      "credit-card": "Thẻ tín dụng",
      "debit-card": "Thẻ ghi nợ",
      paypal: "PayPal",
      "bank-transfer": "Chuyển khoản ngân hàng",
      cod: "Thanh toán khi nhận hàng",
    }
    return methods[method] || method
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-2xl font-bold text-gray-900">TechStore</h1>
            <Link href="/">
              <Button variant="outline" data-testid="home-button">
                <Home className="h-4 w-4 mr-2" />
                Trang chủ
              </Button>
            </Link>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center mb-8">
          <CheckCircle className="h-16 w-16 text-green-500 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-gray-900 mb-2" data-testid="success-title">
            Đặt hàng thành công!
          </h1>
          <p className="text-gray-600" data-testid="success-message">
            Cảm ơn bạn đã mua sắm tại TechStore. Đơn hàng của bạn đang được xử lý.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center">
                <Package className="h-5 w-5 mr-2" />
                Thông tin đơn hàng
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <span className="font-medium">Mã đơn hàng:</span>
                <span className="ml-2" data-testid="order-id">
                  #{order.id}
                </span>
              </div>
              <div>
                <span className="font-medium">Ngày đặt:</span>
                <span className="ml-2" data-testid="order-date">
                  {formatDate(order.orderDate)}
                </span>
              </div>
              <div>
                <span className="font-medium">Trạng thái:</span>
                <span className="ml-2 text-green-600 font-medium" data-testid="order-status">
                  Đã xác nhận
                </span>
              </div>
              <div>
                <span className="font-medium">Tổng tiền:</span>
                <span className="ml-2 text-blue-600 font-bold text-lg" data-testid="order-total">
                  ${order.total}
                </span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center">
                <Truck className="h-5 w-5 mr-2" />
                Thông tin giao hàng
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div>
                <span className="font-medium">Người nhận:</span>
                <span className="ml-2" data-testid="recipient-name">
                  {order.customerInfo.firstName} {order.customerInfo.lastName}
                </span>
              </div>
              <div>
                <span className="font-medium">Địa chỉ:</span>
                <div className="ml-2 text-gray-700" data-testid="shipping-address">
                  {order.customerInfo.address}
                  <br />
                  {order.customerInfo.city}, {order.customerInfo.zipCode}
                </div>
              </div>
              <div>
                <span className="font-medium">Điện thoại:</span>
                <span className="ml-2" data-testid="phone-number">
                  {order.customerInfo.phone}
                </span>
              </div>
              <div>
                <span className="font-medium">Email:</span>
                <span className="ml-2" data-testid="email-address">
                  {order.customerInfo.email}
                </span>
              </div>
              <div>
                <span className="font-medium">Thanh toán:</span>
                <span className="ml-2" data-testid="payment-method">
                  {getPaymentMethodText(order.customerInfo.paymentMethod)}
                </span>
              </div>
            </CardContent>
          </Card>
        </div>

        <Card className="mt-8">
          <CardHeader>
            <CardTitle>Chi tiết sản phẩm</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4" data-testid="order-items">
              {order.items.map((item) => (
                <div key={item.id} className="flex justify-between items-center border-b pb-4">
                  <div>
                    <h3 className="font-medium">{item.name}</h3>
                    <p className="text-gray-600">Số lượng: {item.quantity}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-medium">${item.price * item.quantity}</p>
                    <p className="text-sm text-gray-600">
                      ${item.price} x {item.quantity}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <div className="text-center mt-8 space-y-4">
          <p className="text-gray-600">
            Chúng tôi sẽ gửi email xác nhận và thông tin theo dõi đơn hàng đến địa chỉ email của bạn.
          </p>
          <div className="flex justify-center gap-4">
            <Link href="/">
              <Button variant="outline" data-testid="continue-shopping-button">
                Tiếp tục mua sắm
              </Button>
            </Link>
            <Button data-testid="track-order-button">Theo dõi đơn hàng</Button>
          </div>
        </div>
      </main>
    </div>
  )
}
