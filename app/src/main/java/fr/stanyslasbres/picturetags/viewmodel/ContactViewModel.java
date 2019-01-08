package fr.stanyslasbres.picturetags.viewmodel;

public class ContactViewModel {
    private final long id;
    private String displayName;

    public ContactViewModel(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
