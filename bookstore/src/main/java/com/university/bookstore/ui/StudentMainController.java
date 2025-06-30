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
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

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
    private Button refreshOrderButton;
    @FXML
    private Button viewOrderDetailsButton;
    @FXML
    private Button cancelOrderButton;
    @FXML
    private Button payOrderButton;
    @FXML
    private ComboBox<String> orderStatusFilter;
    @FXML
    private Button filterOrderButton;
    @FXML
    private Button confirmReceiptButton;
    
    // 个人信息相关控件
    @FXML
    private Label headerUsernameLabel;
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
    
    // 自动刷新定时器
    private Timer autoRefreshTimer;
    private static final int AUTO_REFRESH_INTERVAL = 5000; // 5秒自动刷新
    
    // 分页相关字段
    private static final int ITEMS_PER_PAGE = 20; // 每页显示的项目数，增加到20以更好填充列表
    private int currentBookPage = 1; // 当前图书页码
    private int totalBookPages = 1; // 图书总页数
    private int currentOrderPage = 1; // 当前订单页码
    private int totalOrderPages = 1; // 订单总页数
    private String currentSearchKeyword = ""; // 当前搜索关键词
    private String currentOrderFilter = "全部订单"; // 当前订单筛选条件
    
    // 分页控件（需要在FXML中添加）
    @FXML
    private Pagination bookPagination;
    @FXML
    private Pagination orderPagination;
    

    
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
        
        // 初始化分页控件事件
        initializePagination();
        
        // 启动自动刷新
        startAutoRefresh();
    }
    
    @Override
    protected void onUserSet() {
        if (currentUser != null) {
            headerUsernameLabel.setText(currentUser.getUsername());
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
        
        // 设置表格选择模式为单选
        bookTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
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
        
        // 设置表格选择模式为单选
        cartTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
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
        
        // 设置表格选择模式为单选
        orderTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
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
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        quantitySpinner.setEditable(true); // 允许键盘输入
        
        // 添加键盘输入验证
        quantitySpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantitySpinner.getEditor().setText(oldValue);
            } else if (!newValue.isEmpty()) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value < 1) {
                        quantitySpinner.getEditor().setText("1");
                    } else if (value > 999) {
                        quantitySpinner.getEditor().setText("999");
                    }
                } catch (NumberFormatException e) {
                    quantitySpinner.getEditor().setText("1");
                }
            }
        });
        
        // 失去焦点时确保值有效
        quantitySpinner.getEditor().focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) { // 失去焦点
                String text = quantitySpinner.getEditor().getText();
                if (text.isEmpty()) {
                    quantitySpinner.getEditor().setText("1");
                    quantitySpinner.getValueFactory().setValue(1);
                } else {
                    try {
                        int value = Integer.parseInt(text);
                        quantitySpinner.getValueFactory().setValue(value);
                    } catch (NumberFormatException e) {
                        quantitySpinner.getEditor().setText("1");
                        quantitySpinner.getValueFactory().setValue(1);
                    }
                }
            }
        });
        
        // 初始化订单状态筛选下拉框
        orderStatusFilter.setItems(FXCollections.observableArrayList(
            "全部订单", "待支付", "已支付", "已发货", "已完成", "已取消"
        ));
        orderStatusFilter.setValue("全部订单");
        
        // 设置按钮事件
        searchButton.setOnAction(event -> handleSearch());
        addToCartButton.setOnAction(event -> handleAddToCart());
        removeFromCartButton.setOnAction(event -> handleRemoveFromCart());
        clearCartButton.setOnAction(event -> handleClearCart());
        checkoutButton.setOnAction(event -> handleCheckout());
        refreshOrderButton.setOnAction(event -> handleRefreshOrder());
        viewOrderDetailsButton.setOnAction(event -> handleViewOrderDetails());
        cancelOrderButton.setOnAction(event -> handleCancelOrder());
        // 移除筛选按钮事件，改为下拉列表值变化监听
        // filterOrderButton.setOnAction(event -> handleFilterOrders());
        
        // 添加下拉列表值变化监听器，实现自动筛选
        orderStatusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                refreshOrdersWithCurrentFilter();
            }
        });
        confirmReceiptButton.setOnAction(event -> handleConfirmReceipt());
        changePasswordButton.setOnAction(event -> handleChangePassword());
        logoutButton.setOnAction(event -> handleLogout());
        
        // 设置回车搜索
        searchField.setOnAction(event -> handleSearch());
        
        // 监听购物车变化
        cartItems.addListener((javafx.collections.ListChangeListener<CartItem>) change -> updateTotalPrice());
    }
    
    /**
     * 初始化分页控件
     */
    private void initializePagination() {
        // 初始化图书分页控件
        if (bookPagination != null) {
            bookPagination.setPageFactory(pageIndex -> {
                currentBookPage = pageIndex + 1;
                loadBooksWithPaginationOnly();
                return new VBox(); // 返回空的VBox，避免布局问题
            });
        }
        
        // 初始化订单分页控件
        if (orderPagination != null) {
            orderPagination.setPageFactory(pageIndex -> {
                currentOrderPage = pageIndex + 1;
                loadOrdersWithPaginationOnly();
                return new VBox(); // 返回空的VBox，避免布局问题
            });
        }
    }
    
    /**
     * 加载图书数据
     */
    private void loadBooks() {
        currentSearchKeyword = "";
        currentBookPage = 1;
        loadBooksWithPagination();
    }
    
    /**
     * 加载订单数据
     */
    private void loadOrders() {
        if (currentUser == null) return;
        
        // 重置筛选条件为全部订单
        orderStatusFilter.setValue("全部订单");
        currentOrderFilter = "全部订单";
        currentOrderPage = 1;
        loadOrdersWithPagination();
    }
    
    /**
     * 根据当前筛选条件刷新订单数据
     */
    private void refreshOrdersWithCurrentFilter() {
        if (currentUser == null) return;
        
        currentOrderFilter = orderStatusFilter.getValue();
        if (currentOrderFilter == null) currentOrderFilter = "全部订单";
        currentOrderPage = 1; // 重置到第一页
        loadOrdersWithPagination();
    }
    
    /**
     * 分页加载订单数据
     */
    private void loadOrdersWithPagination() {
        if (currentUser == null) return;
        
        try {
            List<Order> allOrders;
            if ("全部订单".equals(currentOrderFilter)) {
                allOrders = orderService.getOrdersByStudentId(currentUser.getId());
            } else {
                allOrders = orderService.getOrdersByStudentIdAndStatus(currentUser.getId(), currentOrderFilter);
            }
            
            // 计算总页数
            totalOrderPages = (int) Math.ceil((double) allOrders.size() / ITEMS_PER_PAGE);
            if (totalOrderPages == 0) totalOrderPages = 1;
            
            // 获取当前页的数据
            int startIndex = (currentOrderPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allOrders.size());
            
            List<Order> pageOrders = allOrders.subList(startIndex, endIndex);
            orderTable.setItems(FXCollections.observableArrayList(pageOrders));
            
            // 更新分页控件
            if (orderPagination != null) {
                orderPagination.setPageCount(totalOrderPages);
                orderPagination.setCurrentPageIndex(currentOrderPage - 1);
            }
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载订单数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 仅加载订单数据，不更新分页控件（避免循环调用）
     */
    private void loadOrdersWithPaginationOnly() {
        if (currentUser == null) return;
        
        try {
            List<Order> allOrders;
            if ("全部订单".equals(currentOrderFilter)) {
                allOrders = orderService.getOrdersByStudentId(currentUser.getId());
            } else {
                allOrders = orderService.getOrdersByStudentIdAndStatus(currentUser.getId(), currentOrderFilter);
            }
            
            // 获取当前页的数据
            int startIndex = (currentOrderPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allOrders.size());
            
            if (startIndex < allOrders.size()) {
                List<Order> pageOrders = allOrders.subList(startIndex, endIndex);
                orderTable.setItems(FXCollections.observableArrayList(pageOrders));
            } else {
                orderTable.setItems(FXCollections.observableArrayList());
            }
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载订单数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理搜索事件
     */
    @FXML
    private void handleSearch() {
        currentSearchKeyword = searchField.getText().trim();
        currentBookPage = 1; // 重置到第一页
        loadBooksWithPagination();
    }
    
    /**
     * 分页加载图书数据
     */
    private void loadBooksWithPagination() {
        try {
            List<Book> allBooks;
            if (currentSearchKeyword.isEmpty()) {
                allBooks = bookService.getAllBooks();
            } else {
                allBooks = bookService.searchBooks(currentSearchKeyword);
            }
            
            // 计算总页数
            totalBookPages = (int) Math.ceil((double) allBooks.size() / ITEMS_PER_PAGE);
            if (totalBookPages == 0) totalBookPages = 1;
            
            // 获取当前页的数据
            int startIndex = (currentBookPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allBooks.size());
            
            List<Book> pageBooks = allBooks.subList(startIndex, endIndex);
            bookTable.setItems(FXCollections.observableArrayList(pageBooks));
            
            // 更新分页控件
            if (bookPagination != null) {
                bookPagination.setPageCount(totalBookPages);
                bookPagination.setCurrentPageIndex(currentBookPage - 1);
            }
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载图书数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 仅加载图书数据，不更新分页控件（避免循环调用）
     */
    private void loadBooksWithPaginationOnly() {
        try {
            List<Book> allBooks;
            if (currentSearchKeyword.isEmpty()) {
                allBooks = bookService.getAllBooks();
            } else {
                allBooks = bookService.searchBooks(currentSearchKeyword);
            }
            
            // 获取当前页的数据
            int startIndex = (currentBookPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allBooks.size());
            
            if (startIndex < allBooks.size()) {
                List<Book> pageBooks = allBooks.subList(startIndex, endIndex);
                bookTable.setItems(FXCollections.observableArrayList(pageBooks));
            } else {
                bookTable.setItems(FXCollections.observableArrayList());
            }
            
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载图书数据失败：" + e.getMessage());
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
        showToastNotification("图书已添加到购物车");
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
     * 处理支付订单事件
     */
    @FXML
    private void handlePayOrder() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要支付的订单");
            return;
        }
        
        if (!orderService.canPayOrder(selectedOrder.getId())) {
            showWarningAlert("无法支付", "该订单当前状态无法支付");
            return;
        }
        
        // 显示支付方式选择对话框
        String paymentMethod = showPaymentMethodDialog();
        if (paymentMethod == null) {
            return; // 用户取消支付
        }
        
        // 直接处理支付
        boolean paymentResult = orderService.payOrder(selectedOrder.getId(), paymentMethod);
        
        if (paymentResult) {
            loadOrders(); // 刷新订单列表
            showInfoAlert("支付成功", 
                String.format("订单 %s 支付成功！\n支付方式：%s\n支付金额：¥%.2f", 
                    selectedOrder.getOrderNumber(), paymentMethod, selectedOrder.getTotalPrice()));
        } else {
            showErrorAlert("支付失败", 
                String.format("订单 %s 支付失败，请重试！\n可能原因：订单状态异常等", 
                    selectedOrder.getOrderNumber()));
        }
    }
    
    /**
     * 显示支付方式选择对话框
     * @return 选择的支付方式，如果取消返回null
     */
    private String showPaymentMethodDialog() {
        // 创建自定义对话框
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("选择支付方式");
        dialog.setHeaderText("请选择您的支付方式");
        
        // 设置按钮类型
        ButtonType confirmButtonType = new ButtonType("确认支付", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        // 创建支付方式选择界面
        VBox content = new VBox(15);
        content.setPadding(new javafx.geometry.Insets(20));
        
        Label titleLabel = new Label("💳 支付方式选择");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ToggleGroup paymentGroup = new ToggleGroup();
        
        // 支付宝选项
        RadioButton alipayRadio = new RadioButton("💰 支付宝支付");
        alipayRadio.setToggleGroup(paymentGroup);
        alipayRadio.setSelected(true); // 默认选择
        alipayRadio.setStyle("-fx-font-size: 14px;");
        
        // 微信支付选项
        RadioButton wechatRadio = new RadioButton("💚 微信支付");
        wechatRadio.setToggleGroup(paymentGroup);
        wechatRadio.setStyle("-fx-font-size: 14px;");
        
        // 银行卡支付选项
        RadioButton bankCardRadio = new RadioButton("🏦 银行卡支付");
        bankCardRadio.setToggleGroup(paymentGroup);
        bankCardRadio.setStyle("-fx-font-size: 14px;");
        
        // 校园卡支付选项
        RadioButton campusCardRadio = new RadioButton("🎓 校园卡支付");
        campusCardRadio.setToggleGroup(paymentGroup);
        campusCardRadio.setStyle("-fx-font-size: 14px;");
        
        content.getChildren().addAll(titleLabel, 
            new Separator(),
            alipayRadio, wechatRadio, bankCardRadio, campusCardRadio);
        
        dialog.getDialogPane().setContent(content);
        
        // 设置结果转换器
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                RadioButton selectedRadio = (RadioButton) paymentGroup.getSelectedToggle();
                if (selectedRadio != null) {
                    String text = selectedRadio.getText();
                    // 提取支付方式名称（去掉emoji）
                    if (text.contains("支付宝")) return "支付宝";
                    if (text.contains("微信")) return "微信支付";
                    if (text.contains("银行卡")) return "银行卡支付";
                    if (text.contains("校园卡")) return "校园卡支付";
                }
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
    

    
    /**
     * 处理修改密码事件
     */
    @FXML
    private void handleChangePassword() {
        showChangePasswordDialog();
    }
    
    /**
     * 显示修改密码对话框
     */
    private void showChangePasswordDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/change_password_dialog.fxml"));
            VBox dialogContent = loader.load();
            
            // 获取对话框中的控件
            PasswordField currentPasswordField = (PasswordField) dialogContent.lookup("#currentPasswordField");
            PasswordField newPasswordField = (PasswordField) dialogContent.lookup("#newPasswordField");
            PasswordField confirmPasswordField = (PasswordField) dialogContent.lookup("#confirmPasswordField");
            Button changeButton = (Button) dialogContent.lookup("#changeButton");
            Button cancelButton = (Button) dialogContent.lookup("#cancelButton");
            
            // 创建新的Stage作为对话框窗口
            Stage dialogStage = new Stage();
            dialogStage.setTitle("修改密码");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(changePasswordButton.getScene().getWindow());
            dialogStage.setResizable(false);
            
            Scene dialogScene = new Scene(dialogContent);
            dialogStage.setScene(dialogScene);
            
            // 设置按钮事件
            changeButton.setOnAction(e -> {
                String currentPassword = currentPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                
                // 验证输入
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showWarningAlert("输入错误", "请填写所有密码字段");
                    return;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    showWarningAlert("密码不匹配", "新密码和确认密码不一致");
                    return;
                }
                
                if (newPassword.length() < 6) {
                    showWarningAlert("密码太短", "新密码长度至少为6位");
                    return;
                }
                
                // 验证当前密码
                 if (!userService.validatePassword(currentUser.getId(), currentPassword)) {
                     showWarningAlert("密码错误", "当前密码不正确");
                     return;
                 }
                
                // 更新密码
                if (userService.updatePassword(currentUser.getId(), newPassword)) {
                    // 先关闭修改密码对话框
                    dialogStage.close();
                    
                    // 显示成功提示
                    showInfoAlert("修改成功", "密码修改成功，请重新登录");
                    
                    // 密码修改成功后返回登录界面（这会关闭当前主窗口）
                    returnToLogin();
                } else {
                    showErrorAlert("修改失败", "密码修改失败，请重试");
                }
            });
            
            cancelButton.setOnAction(e -> dialogStage.close());
            
            // 显示对话框
            dialogStage.showAndWait();
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载修改密码对话框失败：" + e.getMessage());
        }
    }
    

    
    /**
     * 启动自动刷新功能
     */
    private void startAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
        }
        
        autoRefreshTimer = new Timer(true); // 设置为守护线程
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 在JavaFX应用线程中执行刷新操作
                Platform.runLater(() -> {
                    try {
                        // 刷新图书列表（保持当前页和搜索条件）
                        loadBooksWithPagination();
                        // 刷新订单列表（如果当前用户已设置），保持当前筛选状态和页码
                        if (currentUser != null) {
                            loadOrdersWithPagination();
                        }
                    } catch (Exception e) {
                        // 静默处理异常，避免干扰用户操作
                        System.err.println("自动刷新失败: " + e.getMessage());
                    }
                });
            }
        }, AUTO_REFRESH_INTERVAL, AUTO_REFRESH_INTERVAL);
        

    }
    
    /**
     * 停止自动刷新功能
     */
    private void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
            autoRefreshTimer = null;
        }

    }
    
    /**
     * 重启自动刷新功能
     */
    public void restartAutoRefresh() {
        stopAutoRefresh();
        startAutoRefresh();
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
     * @param order 订单对象
     * @param details 订单详情列表
     */
    private void showOrderDetailsDialog(Order order, List<OrderDetail> details) {
        StringBuilder sb = new StringBuilder();
        sb.append("订单号：").append(order.getOrderNumber()).append("\n");
        sb.append("订单状态：").append(formatOrderStatus(order.getStatus())).append("\n");
        sb.append("下单时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getCreateTime())).append("\n");
        sb.append("订单总价：¥").append(order.getTotalPrice()).append("\n\n");
        sb.append("购买的图书详情：\n");
        sb.append("=".repeat(50)).append("\n");
        
        // 调试信息：显示订单详情数量
        sb.append("订单详情数量：").append(details.size()).append("\n");
        
        if (details.isEmpty()) {
            sb.append("暂无订单详情数据\n");
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            // 调试信息：显示订单详情基本信息
            sb.append("订单详情ID：").append(detail.getId()).append("，图书ID：").append(detail.getBookId()).append("\n");
            
            Book book = bookService.getBookById(detail.getBookId());
            if (book != null) {
                sb.append("第").append(i + 1).append("本图书：\n");
                sb.append("  书名：").append(book.getTitle()).append("\n");
                sb.append("  作者：").append(book.getAuthor()).append("\n");
                sb.append("  出版社：").append(book.getPublisher()).append("\n");
                sb.append("  ISBN：").append(book.getIsbn()).append("\n");
                sb.append("  单价：¥").append(detail.getPrice()).append("\n");
                sb.append("  购买数量：").append(detail.getQuantity()).append("本\n");
                sb.append("  小计：¥").append(detail.getSubtotal()).append("\n");
                if (i < details.size() - 1) {
                    sb.append("-".repeat(30)).append("\n");
                }
                totalAmount = totalAmount.add(detail.getSubtotal());
            }
        }
        
        sb.append("=".repeat(50)).append("\n");
        sb.append("总计：¥").append(totalAmount).append("\n");
        sb.append("共购买 ").append(details.size()).append(" 种图书\n");
        
        // 创建自定义对话框以支持滚动
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("订单详情");
        dialog.setHeaderText("订单 " + order.getOrderNumber() + " 的详细信息");
        
        // 创建文本区域并设置为只读
        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(60);
        
        // 创建滚动面板
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefSize(650, 500);
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }
    
    /**
     * 处理刷新订单事件
     */
    private void handleRefreshOrder() {
        loadOrders();
        orderTable.refresh();
        showInfoAlert("刷新成功", "订单列表已刷新");
    }
    
    /**
     * 处理订单筛选事件
     */
    private void handleFilterOrders() {
        refreshOrdersWithCurrentFilter();
    }
    
    /**
     * 处理确认收货事件
     */
    private void handleConfirmReceipt() {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showWarningAlert("请选择订单", "请先选择要确认收货的订单");
            return;
        }
        
        if (selectedOrder.getStatus() != Order.OrderStatus.SHIPPED) {
            showWarningAlert("无法确认收货", "只有已发货的订单才能确认收货");
            return;
        }
        
        // 确认对话框
        boolean confirmed = showConfirmDialog(
            "确认收货", 
            String.format("确认收货订单：%s\n总金额：¥%.2f\n\n确认后订单状态将变为已完成，无法撤销。", 
                selectedOrder.getOrderNumber(), selectedOrder.getTotalPrice())
        );
        
        if (confirmed) {
            try {
                boolean success = orderService.confirmReceipt(selectedOrder.getId());
                if (success) {
                    loadOrders(); // 刷新订单列表
                    showInfoAlert("确认收货成功", 
                        String.format("订单 %s 已确认收货，状态已更新为已完成", selectedOrder.getOrderNumber()));
                } else {
                    showErrorAlert("确认收货失败", "确认收货失败，请重试");
                }
            } catch (Exception e) {
                showErrorAlert("确认收货失败", "确认收货失败：" + e.getMessage());
            }
        }
    }
    
    /**
     * 处理退出登录事件
     */
    private void handleLogout() {
        handleLogout(logoutButton);
    }
    
    /**
     * 返回登录界面
     */
    private void returnToLogin() {
        try {
            // 清除当前用户信息
            currentUser = null;
            
            // 加载登录界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loader.load(), 400, 300);
            
            // 获取当前窗口并切换到登录界面
            Stage currentStage = (Stage) changePasswordButton.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("用户登录");
            currentStage.centerOnScreen();
            
        } catch (IOException e) {
            showErrorAlert("跳转失败", "返回登录界面时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 显示Toast样式的通知
     */
    private void showToastNotification(String message) {
        // 创建通知标签
        Label toastLabel = new Label(message);
        toastLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                           "-fx-padding: 10px 20px; -fx-background-radius: 5px; " +
                           "-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // 获取主界面的根容器（BorderPane）
        BorderPane rootPane = (BorderPane) bookTable.getScene().getRoot();
        
        // 创建一个StackPane来包装通知，以便定位
        StackPane toastContainer = new StackPane();
        toastContainer.getChildren().add(toastLabel);
        toastContainer.setStyle("-fx-background-color: transparent;");
        toastContainer.setMouseTransparent(true);
        
        // 设置通知位置（右上角）
        StackPane.setAlignment(toastLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(toastLabel, new Insets(20, 20, 0, 0));
        
        // 添加到根容器的顶部
        rootPane.setTop(toastContainer);
        
        // 创建淡入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // 创建停留时间
        PauseTransition pause = new PauseTransition(Duration.millis(2000));
        
        // 创建淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            // 恢复原来的顶部内容
            rootPane.setTop(null);
        });
        
        // 按顺序播放动画
        fadeIn.setOnFinished(e -> pause.play());
        pause.setOnFinished(e -> fadeOut.play());
        
        // 开始动画
        fadeIn.play();
    }
}