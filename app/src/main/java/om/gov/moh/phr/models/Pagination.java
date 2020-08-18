package om.gov.moh.phr.models;

import androidx.fragment.app.Fragment;

public class Pagination {
    private int iconId;
    private String title;
    private Fragment fragment;
    private String fragmentTag;

    public Pagination(int iconId, String title, Fragment fragment, String fragmentTag) {
        this.iconId = iconId;
        this.title = title;
        this.fragment = fragment;
        this.fragmentTag = fragmentTag;
    }

    public int getIconId() {
        return iconId;
    }

    public String getTitle() {
        return title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getFragmentTag() {
        return fragmentTag;
    }
}