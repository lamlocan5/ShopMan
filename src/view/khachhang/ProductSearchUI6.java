package view.khachhang;

import dao.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import model.*;

/** Request-scoped View state. Only DAO classes issue SQL. */
public class ProductSearchUI6 {
    private final Connection con;
    private final Account6 sessionAccount;
    private String inKeyword = "";
    private int inPage = 1;
    private List<Product6> outProductList = new ArrayList<>();
    private String outPagination = "";
    private String outMessage = "";
    private String keyword = "";
    private int page = 1;
    private long totalResults;
    private long totalPages;
    private final Map<String, BigDecimal> minimumPrices = new HashMap<>();
    private final Map<String, Boolean> inStock = new HashMap<>();
    private boolean messageVisible;

    public ProductSearchUI6(Connection con, Account6 sessionAccount) {

        this.con=con; this.sessionAccount=sessionAccount;
    }
    public void showResults() throws SQLException {
        clearResults(); outMessage=""; messageVisible=false;
        try { keyword=ProductDAO6.normalizeKeyword(inKeyword); }
        catch (IllegalArgumentException e) { outMessage=e.getMessage(); showMessage(); return; }
        inKeyword=keyword;
        ProductDAO6 productDAO = new ProductDAO6(con,sessionAccount);
        outProductList=productDAO.searchByName(keyword,Math.max(1,inPage));
        totalResults=productDAO.getTotalResultCount();
        totalPages=totalResults==0 ? 0 : (totalResults-1)/ProductDAO6.PAGE_SIZE+1;
        page=(int)Math.min((long)Math.max(1,inPage),Math.max(1,totalPages)); inPage=page;
        CategoryDAO6 categoryDAO=new CategoryDAO6(con,sessionAccount);
        ProductVariantDAO6 variantDAO=new ProductVariantDAO6(con,sessionAccount);
        InventoryDAO6 inventoryDAO=new InventoryDAO6(con,sessionAccount);
        for (Product6 product:outProductList) {
            product.setCategory(categoryDAO.findById(product.getCategory().getCategoryId()));
            List<ProductVariant6> variants=variantDAO.findActiveByProduct(product.getProductId());
            product.setVariants(variants);
            BigDecimal minimum=null; boolean hasStock=false;
            for (ProductVariant6 variant:variants) {
                BigDecimal price=variant.getCurrentPrice();
                if (minimum==null || price.compareTo(minimum)<0) minimum=price;
                if (inventoryDAO.getAvailableQty(variant.getVariantId())>0) hasStock=true;
            }
            minimumPrices.put(product.getProductId(),minimum); inStock.put(product.getProductId(),hasStock);
        }
        outPagination=totalResults+" kết quả, trang "+page+"/"+Math.max(1,totalPages);
        if (outProductList.isEmpty()) { outMessage="Không tìm thấy mặt hàng phù hợp."; showMessage(); }
    }
    public void showMessage() { messageVisible=true; }
    private void clearResults() {
        outProductList=new ArrayList<>(); minimumPrices.clear(); inStock.clear();
        totalResults=0; totalPages=0; page=1; outPagination="";
    }
    public String getInKeyword(){return inKeyword;}
    public void setInKeyword(String value){inKeyword=value==null?"":value;}
    public int getInPage(){return inPage;}
    public void setInPage(int value){inPage=value;}
    public List<Product6> getOutProductList(){return outProductList;}
    public String getOutPagination(){return outPagination;}
    public String getOutMessage(){return outMessage;}
    public void setOutMessage(String value){outMessage=value;}
    public String getKeyword(){return keyword;}
    public int getPage(){return page;}
    public long getTotalResults(){return totalResults;}
    public long getTotalPages(){return totalPages;}
    public BigDecimal getMinimumPrice(String productId){return minimumPrices.get(productId);}
    public boolean isInStock(String productId){return Boolean.TRUE.equals(inStock.get(productId));}
    public boolean isMessageVisible(){return messageVisible;}
}
