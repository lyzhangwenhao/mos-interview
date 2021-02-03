package com.alibaba.mos.interview;

/**
 * ClassName: Position_user
 * Description:
 *
 * @author 张文豪
 * @date 2021/2/1 15:08
 */
public class Position_user {
    private String id;
    private String parent;
    private String name;

    public Position_user() {
    }

    @Override
    public String toString() {
        return "Position_user{" +
                "id='" + id + '\'' +
                ", parent='" + parent + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
