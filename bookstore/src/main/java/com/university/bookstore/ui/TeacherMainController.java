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
 * 教师主界面控制器
 */
public class TeacherMainController extends BaseController implements Initializable {
    
    // 图书浏览相关控件
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> bookTitleColumn;
    @FXML
    private TableColumn<Book, String> bookAuthorColumn;
    @FXML
    private TableColumn<Book, String> bookPublisherColumn;
    @FXML
    private TableColumn<Book, BigDecimal> bookPriceColumn;
    @FXML
    private TableColumn<Book, Integer> bookStockColumn;
    @FXML
    private TextField bookSearchField;
    @FXML
    private Button bookSearchButton;
    @FXML
    private Button viewBookDetailsButton;
    
    // 图书推荐相关控件
    @FXML
    private TableView<Book> recommendBookTable;
    @FXML
    private TableColumn<Book, String> recommendTitleColumn;
    @FXML
    private TableColumn<Book, String> recommendAuthorColumn;
    @FXML
    private TableColumn<Book, String> recommendPublisherColumn;
    @FXML
    private TableColumn<Book, BigDecimal> recommendPriceColumn;
    @FXML
    private TextField recommendSearchField;
    @FXML
    private Button recommendSearchButton;
    @FXML
    private Button addRecommendButton;
    @FXML
    private Button removeRecommendButton;
    
    // 推荐图书表单
    @FXML
    private TextField courseNameField;
    @FXML
    private TextArea recommendReasonArea;
    @FXML
    private Button saveRecommendButton;
    @FXML
    private Button cancelRecommendButton;
    
    // 学生管理相关控件
    @FXML
    private TableView<User> studentTable;
    @FXML
    private TableColumn<User, String> studentNameColumn;
    @FXML
    private TableColumn<User, String> studentIdColumn;
    @FXML
    private TableColumn<User, String> studentUsernameColumn;
    @FXML
    private TableColumn<User, String> studentCreateTimeColumn;
    @FXML
    private TextField studentSearchField;
    @FXML
    private Button studentSearchButton;
    @FXML
    private Button viewStudentOrdersButton;
    
    // 订单查看相关控件
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, String> orderNumberColumn;
    @FXML
    private TableColumn<Order, String> orderStudentColumn;
    @FXML
    private TableColumn<Order, BigDecimal> orderTotalColumn;
    @FXML
    private TableColumn<Order, String> orderStatusColumn;
    @FXML
    private TableColumn<Order, String> orderDateColumn;
    @FXML
    private TextField orderSearchField;
    @FXML
    private Button orderSearchButton;
    @FXML
    private Button viewOrderDetailsButton;
    
    // 统计信息相关控件
    @FXML
    private Label totalStudentsLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label todayOrdersLabel;
    @FXML
    private Label recommendedBooksLabel;
    
    // 个人信息相关控件
    @FXML
    private Label teacherUsernameLabel;
    @FXML
    private Label teacherNameLabel;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button logoutButton;
    
    // 服务类
    private BookService bookService;
    private OrderService orderService;
    private UserService userService;
    
    // 当前选择的图书（用于推荐）
    private Book selectedBookForRecommend;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化服务
        bookService = new BookServiceImpl();
        orderService = new OrderServiceImpl();
        userService = new UserServiceImpl();
        
        // 初始化表格
        initializeBookTable();
        initializeRecommendBookTable();
        initializeStudentTable();
        initializeOrderTable();
        
        // 初始化控件
        initializeControls();
        
        // 加载数据
        loadAllData();
    }
    
    @Override
    protected void onUserSet() {
        if (currentUser != null) {
            teacherUsernameLabel.setText(currentUser.getUsername());
            teacherNameLabel.setText(currentUser.getName());
            loadStatistics();
        }
    }
    
    /**
     * 初始化图书表格
     */
    private void initializeBookTable() {
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookPublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        bookPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        bookStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        // 设置价格格式
        bookPriceColumn.setCellFactory(column -> new TableCell<Book, BigDecimal>() {
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
     * 初始化推荐图书表格
     */
    private void initializeRecommendBookTable() {
        recommendTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        recommendAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        recommendPublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        recommendPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // 设置价格格式
        recommendPriceColumn.setCellFactory(column -> new TableCell<Book, BigDecimal>() {
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
        
        // 设置选择事件
        recommendBookTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedBookForRecommend = newSelection;
            });
    }
    
    /**
     * 初始化学生表格
     */
    private void initializeStudentTable() {
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        studentUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        studentCreateTimeColumn.setCellValueFactory(cellData -> {
            Date createTime = cellData.getValue().getCreateTime();
            if (createTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return new SimpleStringProperty(sdf.format(createTime));
            }
            return new SimpleStringProperty("");
        });
    }
    
    /**
     * 初始化订单表格
     */
    private void initializeOrderTable() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderStudentColumn.setCellValueFactory(cellData -> {
            User student = userService.getUserById(cellData.getValue().getStudentId());
            return new SimpleStringProperty(student != null ? student.getName() : "未知");
        });
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
        // 图书浏览按钮事件
        bookSearchButton.setOnAction(event -> handleBookSearch());
        viewBookDetailsButton.setOnAction(event -> handleViewBookDetails());
        
        // 图书推荐按钮事件
        recommendSearchButton.setOnAction(event -> handleRecommendSearch());
        addRecommendButton.setOnAction(event -> handleAddRecommend());
        removeRecommendButton.setOnAction(event -> handleRemoveRecommend());
        saveRecommendButton.setOnAction(event -> handleSaveRecommend());
        cancelRecommendButton.setOnAction(event -> handleCancelRecommend());
        
        // 学生管理按钮事件
        studentSearchButton.setOnAction(event -> handleStudentSearch());
        viewStudentOrdersButton.setOnAction(event -> handleViewStudentOrders());
        
        // 订单查看按钮事件
        orderSearchButton.setOnAction(event -> handleOrderSearch());
        viewOrderDetailsButton.setOnAction(event -> handleViewOrderDetails());
        
        // 其他按钮事件
        changePasswordButton.setOnAction(event -> handleChangePassword());
        logoutButton.setOnAction(event -> handleLogout());
        
        // 设置回车搜索
        bookSearchField.setOnAction(event -> handleBookSearch());
        recommendSearchField.setOnAction(event -> handleRecommendSearch());
        studentSearchField.setOnAction(event -> handleStudentSearch());
        orderSearchField.setOnAction(event -> handleOrderSearch());
        
        // 初始化推荐表单状态
        setRecommendFormVisible(false);
    }
    
    /**
     * 加载所有数据
     */
    private void loadAllData() {
        loadBooks();
        loadRecommendedBooks();
        loadStudents();
        loadOrders();
        loadStatistics();
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
     * 加载推荐图书数据
     */
    private void loadRecommendedBooks() {
        try {
            // 这里应该加载教师推荐的图书，暂时显示所有图书
            List<Book> books = bookService.getAllBooks();
            recommendBookTable.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载推荐图书数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 加载学生数据
     */
    private void loadStudents() {
        try {
            List<User> students = userService.getAllStudents();
            studentTable.setItems(FXCollections.observableArrayList(students));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载学生数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 加载订单数据
     */
    private void loadOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            orderTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载订单数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 加载统计信息
     */
    private void loadStatistics() {
        try {
            totalStudentsLabel.setText(String.valueOf(userService.getAllStudents().size()));
            totalOrdersLabel.setText(String.valueOf(orderService.getTotalOrderCount()));
            todayOrdersLabel.setText(String.valueOf(orderService.getTodayOrderCount()));
            recommendedBooksLabel.setText("0"); // 暂时设为0，需要实现推荐功能
        } catch (Exception e) {
            showErrorAlert("加载失败", "加载统计信息失败：" + e.getMessage());
        }
    }
    
    // 图书浏览相关方法
    
    @FXML
    private void handleBookSearch() {
        String keyword = bookSearchField.getText().trim();
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
    
    @FXML
    private void handleViewBookDetails() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarningAlert("请选择图书", "请先选择要查看的图书");
            return;
        }
        
        showBookDetailsDialog(selectedBook);
    }
    
    // 图书推荐相关方法
    
    @FXML
    private void handleRecommendSearch() {
        String keyword = recommendSearchField.getText().trim();
        try {
            List<Book> books;
            if (keyword.isEmpty()) {
                books = bookService.getAllBooks();
            } else {
                books = bookService.searchBooks(keyword);
            }
            recommendBookTable.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索图书失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleAddRecommend() {
        Book selectedBook = recommendBookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarningAlert("请选择图书", "请先选择要推荐的图书");
            return;
        }
        
        selectedBookForRecommend = selectedBook;
        setRecommendFormVisible(true);
        courseNameField.clear();
        recommendReasonArea.clear();
    }
    
    @FXML
    private void handleRemoveRecommend() {
        showInfoAlert("功能提示", "移除推荐功能待实现");
    }
    
    @FXML
    private void handleSaveRecommend() {
        if (!validateRecommendForm()) {
            return;
        }
        
        // 这里应该保存推荐信息到数据库
        showInfoAlert("推荐成功", "图书推荐已保存");
        setRecommendFormVisible(false);
    }
    
    @FXML
    private void handleCancelRecommend() {
        setRecommendFormVisible(false);
    }
    
    // 学生管理相关方法
    
    @FXML
    private void handleStudentSearch() {
        String keyword = studentSearchField.getText().trim();
        try {
            List<User> students;
            if (keyword.isEmpty()) {
                students = userService.getAllStudents();
            } else {
                // 根据姓名或学号搜索
                students = userService.getAllStudents().stream()
                    .filter(student -> student.getName().contains(keyword) || 
                                     (student.getStudentId() != null && student.getStudentId().contains(keyword)))
                    .collect(java.util.stream.Collectors.toList());
            }
            studentTable.setItems(FXCollections.observableArrayList(students));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索学生失败：" + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewStudentOrders() {
        User selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showWarningAlert("请选择学生", "请先选择要查看订单的学生");
            return;
        }
        
        try {
            List<Order> studentOrders = orderService.getOrdersByStudentId(selectedStudent.getId());
            orderTable.setItems(FXCollections.observableArrayList(studentOrders));
            showInfoAlert("查看成功", "已显示学生 " + selectedStudent.getName() + " 的订单");
        } catch (Exception e) {
            showErrorAlert("查看失败", "查看学生订单失败：" + e.getMessage());
        }
    }
    
    // 订单查看相关方法
    
    @FXML
    private void handleOrderSearch() {
        String keyword = orderSearchField.getText().trim();
        try {
            List<Order> orders;
            if (keyword.isEmpty()) {
                orders = orderService.getAllOrders();
            } else {
                // 根据订单号搜索
                Order order = orderService.getOrderByOrderNumber(keyword);
                orders = order != null ? Arrays.asList(order) : new ArrayList<>();
            }
            orderTable.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            showErrorAlert("搜索失败", "搜索订单失败：" + e.getMessage());
        }
    }
    
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
    
    @FXML
    private void handleChangePassword() {
        showInfoAlert("功能提示", "修改密码功能待实现");
    }
    
    @FXML
    private void handleLogout() {
        if (showConfirmDialog("确认退出", "确定要退出登录吗？")) {
            showInfoAlert("退出登录", "退出登录功能待实现");
        }
    }
    
    // 辅助方法
    
    /**
     * 设置推荐表单是否可见
     */
    private void setRecommendFormVisible(boolean visible) {
        courseNameField.setVisible(visible);
        recommendReasonArea.setVisible(visible);
        saveRecommendButton.setVisible(visible);
        cancelRecommendButton.setVisible(visible);
    }
    
    /**
     * 验证推荐表单
     */
    private boolean validateRecommendForm() {
        return validateNotEmpty(courseNameField.getText(), "课程名称") &&
               validateNotEmpty(recommendReasonArea.getText(), "推荐理由");
    }
    
    /**
     * 显示图书详情对话框
     */
    private void showBookDetailsDialog(Book book) {
        StringBuilder sb = new StringBuilder();
        sb.append("ISBN：").append(book.getIsbn()).append("\n");
        sb.append("书名：").append(book.getTitle()).append("\n");
        sb.append("作者：").append(book.getAuthor()).append("\n");
        sb.append("出版社：").append(book.getPublisher()).append("\n");
        sb.append("价格：¥").append(book.getPrice()).append("\n");
        sb.append("库存：").append(book.getStock()).append("\n");
        if (book.getDescription() != null && !book.getDescription().trim().isEmpty()) {
            sb.append("描述：").append(book.getDescription()).append("\n");
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("图书详情");
        alert.setHeaderText(book.getTitle());
        alert.setContentText(sb.toString());
        alert.getDialogPane().setPrefWidth(400);
        alert.showAndWait();
    }
    
    /**
     * 显示订单详情对话框
     */
    private void showOrderDetailsDialog(Order order, List<OrderDetail> details) {
        StringBuilder sb = new StringBuilder();
        sb.append("订单号：").append(order.getOrderNumber()).append("\n");
        
        User student = userService.getUserById(order.getStudentId());
        sb.append("学生：").append(student != null ? student.getName() : "未知").append("\n");
        
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