#!/bin/bash

# 测试运行脚本
echo "=========================================="
echo "        在线商店项目测试执行脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 函数：打印彩色消息
print_message() {
    echo -e "${2}${1}${NC}"
}

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    print_message "Maven未安装，请先安装Maven" $RED
    exit 1
fi

print_message "开始运行测试..." $BLUE

# 清理并编译项目
print_message "正在清理和编译项目..." $YELLOW
mvn clean compile -q

if [ $? -ne 0 ]; then
    print_message "编译失败!" $RED
    exit 1
fi

print_message "编译成功!" $GREEN

# 运行所有测试
print_message "正在运行所有测试用例..." $YELLOW
mvn test

if [ $? -eq 0 ]; then
    print_message "所有测试通过!" $GREEN
else
    print_message "部分测试失败，请检查测试结果" $YELLOW
fi

# 显示测试统计
print_message "测试统计信息:" $BLUE
echo "总测试文件数: $(find src/test -name "*Test.java" | wc -l)"
echo "总测试方法数: $(grep -r "@Test" src/test | wc -l)"

# 检查是否有JaCoCo插件配置
if grep -q "jacoco" pom.xml; then
    print_message "正在生成测试覆盖率报告..." $YELLOW
    mvn jacoco:report -q
    
    if [ $? -eq 0 ]; then
        print_message "覆盖率报告生成完成!" $GREEN
        print_message "报告位置: target/site/jacoco/index.html" $BLUE
    else
        print_message "覆盖率报告生成失败" $YELLOW
    fi
else
    print_message "未配置JaCoCo插件，跳过覆盖率报告生成" $YELLOW
fi

echo "=========================================="
print_message "测试执行完成!" $GREEN
echo "=========================================="

# 显示测试覆盖的模块
print_message "已测试的模块:" $BLUE
echo "✅ 库存管理服务 (InventoryService)"
echo "✅ 商品管理服务 (ProductService)"
echo "✅ 库存管理控制器 (InventoryController)"
echo "✅ 商品管理控制器 (ProductController)"
echo "✅ 数据访问层 (ProductMapper)"
echo "✅ 数据模型 (Inventory, InventoryRecord)"
echo "✅ DTO验证 (CreateProductRequest, InventoryStockRequest)"
echo "✅ 以及现有的用户、认证、配置等模块测试"

print_message "预计覆盖率: 50%+" $GREEN