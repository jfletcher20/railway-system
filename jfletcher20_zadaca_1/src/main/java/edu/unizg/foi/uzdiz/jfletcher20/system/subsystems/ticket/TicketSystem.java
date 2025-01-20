package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

public class TicketSystem {
    private String name;
    private String description;
    private String version;
    private String author;
    private String creationDate;
    private String lastModified;
    private String lastModifiedBy;
    private String lastModifiedReason;

    public TicketSystem(String name, String description, String version, String author, String creationDate, String lastModified, String lastModifiedBy, String lastModifiedReason) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.author = author;
        this.creationDate = creationDate;
        this.lastModified = lastModified;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedReason = lastModifiedReason;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public String getLastModifiedReason() {
        return lastModifiedReason;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void setLastModifiedReason(String lastModifiedReason) {
        this.lastModifiedReason = lastModifiedReason;
    }

    @Override
    public String toString() {
        return "TicketSystem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", author='" + author + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedReason='" + lastModifiedReason + '\'' +
                '}';
    }
    
}
