
import BigDecimal;import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bruce.ge on 2017/2/3.
 */
public class Article {

    private Integer id;

    private String uuuuuuu;

    private Long length;

    private Date createTime;

    private Date updateTime;


    private Boolean hasMore;

    private BigDecimal priority;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuuuuuu() {
        return uuuuuuu;
    }

    public void setUuuuuuu(String uuuuuuu) {
        this.uuuuuuu = uuuuuuu;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public BigDecimal getPriority() {
        return priority;
    }

    public void setPriority(BigDecimal priority) {
        this.priority = priority;
    }

    public static void main(String[] args) {
        Article article = new Article();
        article.setId(0);
        article.setUuuuuuu("");
        article.setLength(0L);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        article.setHasMore(false);
        article.setPriority(new BigDecimal());

    }

}
