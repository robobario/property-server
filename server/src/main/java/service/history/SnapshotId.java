package service.history;

import com.google.common.base.Objects;

public class SnapshotId {

    public final long id;


    public SnapshotId(long id) {
        this.id = id;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SnapshotId) {
            final SnapshotId other = (SnapshotId) obj;
            return Objects.equal(id, other.id);
        }
        else {
            return false;
        }
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("id", id).toString();
    }


    public long longValue() {
        return id;
    }
}
