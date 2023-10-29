
import Integer;
import String;
import Boolean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import Date;
import BigDecimal;import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class AssertAllGetterWithDefault {

    public static class People {

        private String name;

        private Integer age;

        private Date birth;

        private Boolean sex;

        private BigDecimal length;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Date getBirth() {
            return birth;
        }

        public void setBirth(Date birth) {
            this.birth = birth;
        }

        public Boolean getSex() {
            return sex;
        }

        public void setSex(Boolean sex) {
            this.sex = sex;
        }

        public BigDecimal getLength() {
            return length;
        }

        public void setLength(BigDecimal length) {
            this.length = length;
        }

    }

    @Test
    void testMethod() {

        People people = new People();
        assertEquals(new String(), people.getName());
        assertEquals(new Integer(), people.getAge());
        assertEquals(new Date(), people.getBirth());
        assertEquals(new Boolean(), people.getSex());
        assertEquals(new BigDecimal(), people.getLength());


    }

}