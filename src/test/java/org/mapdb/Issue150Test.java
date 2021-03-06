package org.mapdb;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.junit.Test;

public class Issue150Test {

    @Test
    public void test() {
        // TxMaker txMaker = DBMaker.newFileDB(new File("/tmp/mapdb.test"))
        // .closeOnJvmShutdown().asyncWriteDisable().makeTxMaker();
        TxMaker txMaker = DBMaker.newMemoryDB().closeOnJvmShutdown()
                .asyncWriteDisable().makeTxMaker();

        DB db = txMaker.makeTx();

        EntityA x = new EntityA();
        x.setId(126l);
        x.setName("nameXXX");

        Serializer<EntityA> valueSerializer = new CustomSerializer();
        Map<Long, EntityA> map = db.createHashMap("entitya", false, null,
                valueSerializer);
        map.put(x.getId(), x);

        db.commit();

        EntityA y = (EntityA) txMaker.makeTx().getHashMap("entitya")
                .get(x.getId());
        System.out.println(x.equals(y));

        txMaker.close();
    }

    private static final class CustomSerializer implements
            Serializer<Issue150Test.EntityA>, Serializable {

        @Override
        public void serialize(DataOutput out, EntityA value) throws IOException {
            out.writeLong(value.getId());
            out.writeUTF(value.getName());
        }

        @Override
        public EntityA deserialize(DataInput in, int available)
                throws IOException {

            EntityA a = new EntityA();
            a.setId(in.readLong());
            a.setName(in.readUTF());
            return a;
        }
    }

    public static class EntityA implements Serializable {

        private Long id;

        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EntityA other = (EntityA) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

    }

}
