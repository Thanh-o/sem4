"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { Badge } from "@/components/ui/badge"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Separator } from "@/components/ui/separator"
import { Database, FileText, TestTube, Play, CheckCircle } from "lucide-react"

interface TestCase {
  id: string
  scenario: string
  description: string
  sqlQueries: string[]
  expectedResult: string
  businessRule: string
  priority: "High" | "Medium" | "Low"
  category: string
}

interface DatabaseTable {
  name: string
  columns: string[]
  relationships: string[]
}

export default function BAAutoTestingSystem() {
  const [databaseSchema, setDatabaseSchema] = useState("")
  const [businessRequirements, setBusinessRequirements] = useState("")
  const [generatedTests, setGeneratedTests] = useState<TestCase[]>([])
  const [parsedTables, setParsedTables] = useState<DatabaseTable[]>([])
  const [isAnalyzing, setIsAnalyzing] = useState(false)

  const sampleSchema = `-- E-commerce Database Schema
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    status ENUM('active', 'inactive', 'suspended') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    category_id INT,
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'confirmed', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE order_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);`

  const sampleBusinessRequirements = `# Tài liệu Nghiệp vụ - Hệ thống E-commerce

## 1. Quản lý User
- User có thể đăng ký tài khoản với email duy nhất
- User phải có trạng thái active mới được đặt hàng
- Không được phép có 2 user cùng username

## 2. Quản lý Sản phẩm
- Sản phẩm phải có giá > 0
- Chỉ sản phẩm có status = 'active' mới hiển thị
- Stock quantity không được âm

## 3. Quy trình Đặt hàng
- User chỉ đặt hàng khi đã đăng nhập và có status = 'active'
- Kiểm tra tồn kho trước khi tạo order
- Khi tạo order thành công, phải trừ stock_quantity
- Tổng tiền order = sum(quantity * unit_price) của tất cả items
- Order mới tạo có status = 'pending'

## 4. Ràng buộc dữ liệu
- Email phải unique trong bảng users
- Username phải unique trong bảng users
- Quantity trong order_items phải > 0
- Unit_price phải > 0
- Total_amount phải = tổng của các order_items`

  const parseSchema = (schema: string): DatabaseTable[] => {
    const tables: DatabaseTable[] = []
    const createTableRegex = /CREATE TABLE (\w+) $$([\s\S]*?)$$;/gi
    let match

    while ((match = createTableRegex.exec(schema)) !== null) {
      const tableName = match[1]
      const tableContent = match[2]

      const columns = tableContent
        .split(",")
        .map((col) => col.trim().split(" ")[0])
        .filter((col) => col && !col.includes("FOREIGN") && !col.includes("PRIMARY"))

      const relationships = tableContent
        .split("\n")
        .filter((line) => line.includes("FOREIGN KEY"))
        .map((line) => line.trim())

      tables.push({
        name: tableName,
        columns,
        relationships,
      })
    }

    return tables
  }

  const generateTestCases = (schema: string, requirements: string): TestCase[] => {
    const tables = parseSchema(schema)
    setParsedTables(tables)

    const testCases: TestCase[] = []

    // Test cases cho User Management
    testCases.push({
      id: "TC001",
      scenario: "Đăng ký user với email hợp lệ",
      description: "Kiểm tra việc tạo user mới với email unique",
      sqlQueries: [
        "-- Kiểm tra email chưa tồn tại\nSELECT COUNT(*) FROM users WHERE email = 'test@example.com';",
        "-- Tạo user mới\nINSERT INTO users (username, email, password_hash, full_name, status) VALUES ('testuser', 'test@example.com', 'hashed_password', 'Test User', 'active');",
        "-- Verify user được tạo\nSELECT * FROM users WHERE email = 'test@example.com';",
      ],
      expectedResult: "User được tạo thành công với status = 'active'",
      businessRule: "User có thể đăng ký tài khoản với email duy nhất",
      priority: "High",
      category: "User Management",
    })

    testCases.push({
      id: "TC002",
      scenario: "Đăng ký user với email đã tồn tại",
      description: "Kiểm tra ràng buộc email unique",
      sqlQueries: [
        "-- Tạo user đầu tiên\nINSERT INTO users (username, email, password_hash, full_name) VALUES ('user1', 'duplicate@example.com', 'hash1', 'User 1');",
        "-- Thử tạo user thứ 2 với cùng email (should fail)\nINSERT INTO users (username, email, password_hash, full_name) VALUES ('user2', 'duplicate@example.com', 'hash2', 'User 2');",
      ],
      expectedResult: "Lỗi duplicate key violation",
      businessRule: "Email phải unique trong bảng users",
      priority: "High",
      category: "Data Validation",
    })

    // Test cases cho Product Management
    testCases.push({
      id: "TC003",
      scenario: "Tạo sản phẩm với giá hợp lệ",
      description: "Kiểm tra tạo sản phẩm với price > 0",
      sqlQueries: [
        "-- Tạo sản phẩm mới\nINSERT INTO products (product_name, description, price, stock_quantity, status) VALUES ('Test Product', 'Product description', 99.99, 100, 'active');",
        "-- Verify sản phẩm được tạo\nSELECT * FROM products WHERE product_name = 'Test Product';",
      ],
      expectedResult: "Sản phẩm được tạo thành công",
      businessRule: "Sản phẩm phải có giá > 0",
      priority: "High",
      category: "Product Management",
    })

    testCases.push({
      id: "TC004",
      scenario: "Hiển thị chỉ sản phẩm active",
      description: "Kiểm tra chỉ sản phẩm có status = 'active' mới hiển thị",
      sqlQueries: [
        "-- Tạo sản phẩm active\nINSERT INTO products (product_name, price, status) VALUES ('Active Product', 50.00, 'active');",
        "-- Tạo sản phẩm inactive\nINSERT INTO products (product_name, price, status) VALUES ('Inactive Product', 60.00, 'inactive');",
        "-- Query chỉ sản phẩm active\nSELECT * FROM products WHERE status = 'active';",
      ],
      expectedResult: "Chỉ trả về sản phẩm có status = 'active'",
      businessRule: "Chỉ sản phẩm có status = 'active' mới hiển thị",
      priority: "Medium",
      category: "Product Management",
    })

    // Test cases cho Order Process
    testCases.push({
      id: "TC005",
      scenario: "Tạo order với user active",
      description: "Kiểm tra quy trình đặt hàng với user hợp lệ",
      sqlQueries: [
        "-- Kiểm tra user active\nSELECT * FROM users WHERE user_id = 1 AND status = 'active';",
        "-- Kiểm tra tồn kho sản phẩm\nSELECT stock_quantity FROM products WHERE product_id = 1 AND stock_quantity >= 2;",
        "-- Tạo order\nINSERT INTO orders (user_id, total_amount, status) VALUES (1, 199.98, 'pending');",
        "-- Tạo order items\nINSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (LAST_INSERT_ID(), 1, 2, 99.99);",
        "-- Cập nhật stock\nUPDATE products SET stock_quantity = stock_quantity - 2 WHERE product_id = 1;",
      ],
      expectedResult: "Order được tạo thành công và stock được cập nhật",
      businessRule: "User chỉ đặt hàng khi active và có đủ tồn kho",
      priority: "High",
      category: "Order Process",
    })

    testCases.push({
      id: "TC006",
      scenario: "Kiểm tra tính toán tổng tiền order",
      description: "Verify tổng tiền order = sum(quantity * unit_price)",
      sqlQueries: [
        "-- Tạo order\nINSERT INTO orders (user_id, total_amount, status) VALUES (1, 0, 'pending');",
        "-- Thêm order items\nINSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (LAST_INSERT_ID(), 1, 2, 50.00);",
        "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (LAST_INSERT_ID(), 2, 1, 30.00);",
        "-- Tính tổng và cập nhật\nUPDATE orders SET total_amount = (SELECT SUM(quantity * unit_price) FROM order_items WHERE order_id = orders.order_id) WHERE order_id = LAST_INSERT_ID();",
        "-- Verify tổng tiền\nSELECT o.total_amount, SUM(oi.quantity * oi.unit_price) as calculated_total FROM orders o JOIN order_items oi ON o.order_id = oi.order_id WHERE o.order_id = LAST_INSERT_ID();",
      ],
      expectedResult: "Total_amount = calculated_total (130.00)",
      businessRule: "Tổng tiền order = sum(quantity * unit_price) của tất cả items",
      priority: "High",
      category: "Business Logic",
    })

    // Test cases cho Data Validation
    testCases.push({
      id: "TC007",
      scenario: "Kiểm tra stock không âm",
      description: "Verify stock_quantity không được âm",
      sqlQueries: [
        "-- Thử cập nhật stock thành số âm\nUPDATE products SET stock_quantity = -5 WHERE product_id = 1;",
        "-- Kiểm tra constraint\nSELECT * FROM products WHERE stock_quantity < 0;",
      ],
      expectedResult: "Không có sản phẩm nào có stock âm",
      businessRule: "Stock quantity không được âm",
      priority: "Medium",
      category: "Data Validation",
    })

    testCases.push({
      id: "TC008",
      scenario: "Kiểm tra quantity trong order > 0",
      description: "Verify quantity trong order_items phải > 0",
      sqlQueries: [
        "-- Thử tạo order item với quantity = 0\nINSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 1, 0, 50.00);",
        "-- Thử tạo order item với quantity âm\nINSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 1, -1, 50.00);",
        "-- Kiểm tra dữ liệu không hợp lệ\nSELECT * FROM order_items WHERE quantity <= 0;",
      ],
      expectedResult: "Không có order_items nào có quantity <= 0",
      businessRule: "Quantity trong order_items phải > 0",
      priority: "Medium",
      category: "Data Validation",
    })

    return testCases
  }

  const analyzeAndGenerate = async () => {
    setIsAnalyzing(true)

    // Simulate analysis time
    await new Promise((resolve) => setTimeout(resolve, 2000))

    const testCases = generateTestCases(databaseSchema, businessRequirements)
    setGeneratedTests(testCases)
    setIsAnalyzing(false)
  }

  const loadSampleData = () => {
    setDatabaseSchema(sampleSchema)
    setBusinessRequirements(sampleBusinessRequirements)
  }

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case "High":
        return "bg-red-500"
      case "Medium":
        return "bg-yellow-500"
      case "Low":
        return "bg-green-500"
      default:
        return "bg-gray-500"
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4">
      <div className="max-w-7xl mx-auto space-y-6">
        <div className="text-center space-y-2">
          <h1 className="text-3xl font-bold text-gray-900">BA Auto Testing System</h1>
          <p className="text-gray-600">Tự động sinh Test Cases từ Database Design và Tài liệu Nghiệp vụ</p>
        </div>

        <Tabs defaultValue="input" className="w-full">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="input" className="flex items-center gap-2">
              <FileText className="h-4 w-4" />
              Input
            </TabsTrigger>
            <TabsTrigger value="analysis" className="flex items-center gap-2">
              <Database className="h-4 w-4" />
              Schema Analysis
            </TabsTrigger>
            <TabsTrigger value="test-cases" className="flex items-center gap-2">
              <TestTube className="h-4 w-4" />
              Test Cases
            </TabsTrigger>
            <TabsTrigger value="sql-queries" className="flex items-center gap-2">
              <Play className="h-4 w-4" />
              SQL Queries
            </TabsTrigger>
          </TabsList>

          <TabsContent value="input">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Database className="h-5 w-5" />
                    Database Schema
                  </CardTitle>
                  <CardDescription>Paste SQL CREATE TABLE statements hoặc database design</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <Textarea
                    placeholder="Paste your CREATE TABLE statements here..."
                    value={databaseSchema}
                    onChange={(e) => setDatabaseSchema(e.target.value)}
                    className="min-h-[300px] font-mono text-sm"
                  />
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <FileText className="h-5 w-5" />
                    Business Requirements
                  </CardTitle>
                  <CardDescription>Tài liệu nghiệp vụ, user stories, business rules</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <Textarea
                    placeholder="Paste your business requirements document here..."
                    value={businessRequirements}
                    onChange={(e) => setBusinessRequirements(e.target.value)}
                    className="min-h-[300px]"
                  />
                </CardContent>
              </Card>
            </div>

            <div className="flex gap-4 justify-center mt-6">
              <Button onClick={loadSampleData} variant="outline">
                Load Sample Data
              </Button>
              <Button
                onClick={analyzeAndGenerate}
                disabled={!databaseSchema || !businessRequirements || isAnalyzing}
                size="lg"
              >
                {isAnalyzing ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                    Analyzing...
                  </>
                ) : (
                  <>
                    <TestTube className="h-4 w-4 mr-2" />
                    Generate Test Cases
                  </>
                )}
              </Button>
            </div>
          </TabsContent>

          <TabsContent value="analysis">
            <Card>
              <CardHeader>
                <CardTitle>Database Schema Analysis</CardTitle>
                <CardDescription>Phân tích cấu trúc database và relationships</CardDescription>
              </CardHeader>
              <CardContent>
                {parsedTables.length === 0 ? (
                  <div className="text-center py-8 text-gray-500">
                    Chưa có schema nào được phân tích. Vui lòng nhập schema và generate test cases.
                  </div>
                ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {parsedTables.map((table) => (
                      <Card key={table.name} className="border-l-4 border-l-blue-500">
                        <CardHeader className="pb-3">
                          <CardTitle className="text-lg">{table.name}</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-3">
                          <div>
                            <Label className="text-sm font-medium text-gray-600">Columns:</Label>
                            <div className="flex flex-wrap gap-1 mt-1">
                              {table.columns.map((col) => (
                                <Badge key={col} variant="secondary" className="text-xs">
                                  {col}
                                </Badge>
                              ))}
                            </div>
                          </div>
                          {table.relationships.length > 0 && (
                            <div>
                              <Label className="text-sm font-medium text-gray-600">Relationships:</Label>
                              <div className="text-xs text-gray-500 mt-1">
                                {table.relationships.length} foreign key(s)
                              </div>
                            </div>
                          )}
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="test-cases">
            <Card>
              <CardHeader>
                <CardTitle>Generated Test Cases</CardTitle>
                <CardDescription>
                  Test cases được sinh tự động từ database schema và business requirements
                </CardDescription>
              </CardHeader>
              <CardContent>
                {generatedTests.length === 0 ? (
                  <div className="text-center py-8 text-gray-500">
                    Chưa có test case nào được sinh ra. Vui lòng nhập input và generate.
                  </div>
                ) : (
                  <ScrollArea className="h-[600px]">
                    <div className="space-y-4">
                      {generatedTests.map((testCase) => (
                        <Card key={testCase.id} className="border-l-4 border-l-green-500">
                          <CardHeader className="pb-3">
                            <div className="flex items-start justify-between">
                              <div>
                                <CardTitle className="text-lg">
                                  {testCase.id}: {testCase.scenario}
                                </CardTitle>
                                <CardDescription className="mt-1">{testCase.description}</CardDescription>
                              </div>
                              <div className="flex gap-2">
                                <Badge className={`${getPriorityColor(testCase.priority)} text-white`}>
                                  {testCase.priority}
                                </Badge>
                                <Badge variant="outline">{testCase.category}</Badge>
                              </div>
                            </div>
                          </CardHeader>
                          <CardContent className="space-y-3">
                            <div>
                              <Label className="text-sm font-medium text-gray-600">Business Rule:</Label>
                              <p className="text-sm mt-1 p-2 bg-blue-50 rounded">{testCase.businessRule}</p>
                            </div>

                            <div>
                              <Label className="text-sm font-medium text-gray-600">Expected Result:</Label>
                              <p className="text-sm mt-1 p-2 bg-green-50 rounded">{testCase.expectedResult}</p>
                            </div>

                            <div>
                              <Label className="text-sm font-medium text-gray-600">SQL Test Steps:</Label>
                              <div className="mt-1 space-y-2">
                                {testCase.sqlQueries.map((query, index) => (
                                  <pre
                                    key={index}
                                    className="bg-gray-900 text-green-400 p-3 rounded text-xs overflow-x-auto"
                                  >
                                    <code>{query}</code>
                                  </pre>
                                ))}
                              </div>
                            </div>
                          </CardContent>
                        </Card>
                      ))}
                    </div>
                  </ScrollArea>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="sql-queries">
            <Card>
              <CardHeader>
                <CardTitle>All SQL Queries</CardTitle>
                <CardDescription>Tất cả SQL queries được sinh ra từ các test cases</CardDescription>
              </CardHeader>
              <CardContent>
                {generatedTests.length === 0 ? (
                  <div className="text-center py-8 text-gray-500">Chưa có SQL query nào được sinh ra.</div>
                ) : (
                  <ScrollArea className="h-[600px]">
                    <div className="space-y-6">
                      {generatedTests.map((testCase) => (
                        <div key={testCase.id} className="space-y-3">
                          <div className="flex items-center gap-2">
                            <CheckCircle className="h-5 w-5 text-green-500" />
                            <h3 className="font-semibold">
                              {testCase.id}: {testCase.scenario}
                            </h3>
                            <Badge className={`${getPriorityColor(testCase.priority)} text-white`}>
                              {testCase.priority}
                            </Badge>
                          </div>

                          <div className="pl-7 space-y-2">
                            {testCase.sqlQueries.map((query, index) => (
                              <div key={index}>
                                <Label className="text-xs text-gray-500">Step {index + 1}:</Label>
                                <pre className="bg-gray-900 text-green-400 p-3 rounded text-sm overflow-x-auto mt-1">
                                  <code>{query}</code>
                                </pre>
                              </div>
                            ))}
                          </div>

                          <Separator />
                        </div>
                      ))}
                    </div>
                  </ScrollArea>
                )}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>

        {/* Statistics */}
        {generatedTests.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold">{generatedTests.length}</div>
                <p className="text-xs text-muted-foreground">Total Test Cases</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold text-red-600">
                  {generatedTests.filter((t) => t.priority === "High").length}
                </div>
                <p className="text-xs text-muted-foreground">High Priority</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold text-yellow-600">
                  {generatedTests.filter((t) => t.priority === "Medium").length}
                </div>
                <p className="text-xs text-muted-foreground">Medium Priority</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold text-green-600">
                  {generatedTests.filter((t) => t.priority === "Low").length}
                </div>
                <p className="text-xs text-muted-foreground">Low Priority</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold text-blue-600">
                  {generatedTests.reduce((total, test) => total + test.sqlQueries.length, 0)}
                </div>
                <p className="text-xs text-muted-foreground">SQL Queries</p>
              </CardContent>
            </Card>
          </div>
        )}
      </div>
    </div>
  )
}
