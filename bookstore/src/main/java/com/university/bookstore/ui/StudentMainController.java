package com.university.bookstore.ui;

import com.university.bookstore.model.*;
import com.university.bookstore.service.*;
import com.university.bookstore.service.impl.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 学生主界面控制器
 */
public class StudentMainController extends BaseController implements Initializable {
    
    // 图书浏览相关控件
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> publisherColumn;
    @FXML
    private TableColumn<Book, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Book, Integer> stockColumn;
    @FXML
    private Button addToCartButton;
    @FXML
    private Spinner<Integer> quantitySpinner;
    
    // 购物车相关控件
    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableColumn<CartItem, String> cartTitleColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> cartPriceColumn;
    @FXML
    private TableColumn<CartItem, Integer> cartQuantityColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> cartSubtotalColumn;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button removeFromCartButton;
    @FXML
    private Button clearCartButton;
    @FXML
    private Button checkoutButton;
    
    // 订单管理相关控件
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, String> orderNumberColumn;
    @FXML
    private TableColumn<Order, BigDecimal> orderTotalColumn;
    @FXML
    private TableColumn<Order, String> orderStatusColumn;
    @FXML
    private TableColumn<Order, String> orderDateColumn;
    @FXML
    private Button viewOrderDetailsButton;
    @FXML
    private Button cancelOrderButton;
    
    // 个人信息相关控件
    @FXML
    private Label usernameLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label studentIdLabel;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button logoutButton;
    
    // 服务类
    private BookService bookService;
    private OrderService orderService;
    private UserService userService;
    
    // 购物车数据
    private ObservableList<CartItem> cartItems;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化服务
        bookService = new BookServiceImpl();
        orderService = new OrderServiceImpl();
        userService = new UserServiceImpl();
        
        // 初始化购物车
        cartItems = FXCollections.observableArrayList();
        
        // 初始化表格
        initializeBookTable();
        initializeCartTable();
        initializeOrderTable();
        
        // 初始化控件
        initializeControls();
        
        // 加载数据
        loadBooks();
    }
    
    @Override
    protected void onUserSet() {
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            nameLabel.setText(currentUser.getName());
            studentIdLabel.setText(currentUser.getStudentId() != null ? currentUser.getStudentId() : "无");
            loadOrders();
        }
    }
    
    /**
     * 初始化图书表格
     */
    private void initializeBookTable() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        // 设置价格格式
        priceColumn.setCellFactory(column -> new TableCell<Book, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("¥%.2f", price));
                }
            }
        });
    }
    
    /**
     * 初始化购物车表格
     */
    private void initializeCartTable() {
        cartTitleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook().getTitle()));
        cartPriceColumn.setCellValueFactory(cellData -> 
            cellData.getValue().getBook().priceProperty());
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartSubtotalColumn.setCellValueFactory(cellData -> 
            cellData.getValue().subtotalProperty());
        
        // 设置价格格式
        cartPriceColumn.setCellFactory(column -> new TableCell<CartItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("¥%.2f", price));
                }
            }
        });
        
        cartSubtotalColumn.setCellFactory(column -> new TableCell<CartItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("¥%.2f", subtotal));
                }
            }
        });
        
        cartTable.setItems(cartItems);
    }
    
    /**
     * 初始化订单表格
     */
    private void initializeOrderTable() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderStatusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatOrderStatus(cellData.getValue().getStatus())));
        orderDateColumn.setCellValueFactory(cellData -> {
            Date createTime = cellData.getValue().getCreateTime();
            if (createTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return new SimpleStringProperty(sdf.format(createTime));
            }
            return new SimpleStringProperty("");
        });
        
        // 设置价格格式
        orderTotalColumn.setCellFactory(column -> new TableCell<Order, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("¥%.2f", price));
                }
            }
        });
    }
    
    /**
     * 初始化控件
     */
    private void initializeControls() {
        // 初始化数量选择器
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));
        
        // 设置按钮事件
        searchButton.setOnAction(event -> handleSearch());
        addToCartButton.setOnAction(event -> handleAddToCart());
        removeFromCartButton.setOnAction(event -> handleRemoveFromCart());
        clearCartButton.setOnAction(event -> handleClearCart());
        checkoutButton.setOnAction(event -> handleCheckout());
        viewOrderDetailsButton.setOnAction(event -> handleViewOrderDetails());
        cancelOrderButton.setOnAction(event -> handleCancelOrder());
        changePasswordButton.setOnAction(event -> handleChangePassword());
        logoutButton.setOnAction(event -> handleLogout());
        
        // 设置回车搜索
        searchField.setOnAction(event -> handleSearch());
        
        // 监听购物车变化
        cartItems.addListener((javafx.collections.ListChangeListener<CartItem>) change -> updateTotalPrice());
    }
    
    /**
     * 加载图书数据
     */
    private void loadBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            bookTable.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载图书数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 加载订单数据
     */
    private void loadOrders() {
        if (currentUser == null) return;
        
        try {
            List<Order> orders = orderService.getOrdersByStudentId(currentUser.getId());
            orderTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载订单数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理搜索事件
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        try {
            List<Book> books;
            if (keyword.isEmpty()) {
                books = bookService.getAllBooks();
            } else {
                books = bookService.searchBooks(keyword);
            }
            bookTable.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索图书失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理添加到购物车事件
     */
    @FXML
    private void handleAddToCart() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarningAlert("请选择图书", "请先选择要添加到购物车的图书");
            return;
        }
        
        int quantity = quantitySpinner.getValue();
        
        // 检查库存
        if (!bookService.checkBookStock(selectedBook.getId(), quantity)) {
            showWarningAlert("库存不足", "图书库存不足，无法添加到购物车");
            return;
        }
        
        // 检查购物车中是否已有该图书
        CartItem existingItem = cartItems.stream()
            .filter(item -> item.getBook().getId().equals(selectedBook.getId()))
            .findFirst()
            .orElse(null);
        
        if (existingItem != null) {
            // 增加数量
            int newQuantity = existingItem.getQuantity() + quantity;
            if (!bookService.checkBookStock(selectedBook.getId(), newQuantity)) {
                showWarningAlert("库存不足", "图书库存不足，无法增加数量");
                return;
            }
            existingItem.setQuantity(newQuantity);
        } else {
            // 添加新项目
            CartItem newItem = new CartItem(selectedBook, quantity);
            cartItems.add(newItem);
        }
        
        cartTable.refresh();
        showInfoAlert("添加成功", "图书已添加到购物车");
    }
    
    /**
     * 处理从购物车移除事件
     */
    @FXML
    private void handleRemoveFromCart() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showWarningAlert("请选择项目", "请先选择要移除的购物车项目");
            return;
        }
        
        cartItems.remove(selectedItem);
        showInfoAlert("移除成功", "项目已从购物车移除");
    }
    
    /**
     * 处理清空购物车事件
     */
    @FXML
    private void handleClearCart() {
        if (cartItems.isEmpty()) {
            showWarningAlert("购物车为空", "购物车中没有商品");
            return;
        }
        
        if (showConfirmDialog("确认清空", "确定要清空购物车吗？")) {
            cartItems.clear();
            showInfoAlert("清空成功", "购物车已清空");
        }
    }
    
    /**
     * 处理结算事件
     */
    @FXML
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            showWarningAlert("购物车为空", "购物车中没有商品，无法结算");
            return;
        }
        
        try {
            // 验证库存
            if (!orderService.validateCartStock(new ArrayList<>(cartItems))) {
                showWarningAlert("库存不足", "部分商品库存不足，请调整购买数量");
                return;
            }
            
            // 创建订单
            Order order = orderService.createOrder(currentUser.getId(), new ArrayList<>(cartItems));
            if (order != null) {
                cartItems.clear();
                loadOrders();
                showInfoAlert("下单成功", "订单创建成功！订单号：" + order.getOrderNumber());
            } else {
                showErrorAlert("下单失败", "创建订单失败，请重试");
            }
        } catch (Exception e) {
            showErrorAlert("下单失败", "创建订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理查看订单详情事件
     */
    @FXML
    private void handleViewOrderDetails() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要查看的订单");
            return;
        }
        
        try {
            List<OrderDetail> details = orderService.getOrderDetails(selectedOrder.getId());
            showOrderDetailsDialog(selectedOrder, details);
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载订单详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理取消订单事件
     */
    @FXML
    private void handleCancelOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要取消的订单");
            return;
        }
        
        if (!orderService.canCancelOrder(selectedOrder.getId())) {
            showWarningAlert("无法取消", "该订单当前状态无法取消");
            return;
        }
        
        if (showConfirmDialog("确认取消", "确定要取消订单 " + selectedOrder.getOrderNumber() + " 吗？")) {
            try {
                if (orderService.cancelOrder(selectedOrder.getId(), currentUser.getId())) {
                    loadOrders();
                    showInfoAlert("取消成功", "订单已取消");
                } else {
                    showErrorAlert("取消失败", "取消订单失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("取消失败", "取消订单失败：" + e.getMessage());
            }
        }
    }
    
    /**
     * 处理修改密码事件
     */
    @FXML
    private void handleChangePassword() {
        // 这里可以打开修改密码对话框
        showInfoAlert("功能提示", "修改密码功能待实现");
    }
    
    /**
     * 处理退出登录事件
     */
    @FXML
    private void handleLogout() {
        if (showConfirmDialog("确认退出", "确定要退出登录吗？")) {
            // 关闭当前窗口，返回登录界面
            // 这里需要实现返回登录界面的逻辑
            showInfoAlert("退出登录", "退出登录功能待实现");
        }
    }
    
    /**
     * 更新总价
     */
    private void updateTotalPrice() {
        BigDecimal total = orderService.calculateCartTotal(new ArrayList<>(cartItems));
        totalPriceLabel.setText(String.format("总计：¥%.2f", total));
    }
    
    /**
     * 显示订单详情对话框
     */
    private void showOrderDetailsDialog(Order order, List<OrderDetail> details) {
        StringBuilder sb = new StringBuilder();
        sb.append("订单号：").append(order.getOrderNumber()).append("\n");
        sb.append("订单状态：").append(formatOrderStatus(order.getStatus())).append("\n");
        sb.append("下单时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime())).append("\n");
        sb.append("订单总价：¥").append(order.getTotalPrice()).append("\n\n");
        sb.append("订单详情：\n");
        
        for (OrderDetail detail : details) {
            Book book = bookService.getBookById(detail.getBookId());
            if (book != null) {
                sb.append("- ").append(book.getTitle())
                  .append(" × ").append(detail.getQuantity())
                  .append(" = ¥").append(detail.getSubtotal()).append("\n");
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("订单详情");
        alert.setHeaderText(null);
        alert.setContentText(sb.toString());
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }
}