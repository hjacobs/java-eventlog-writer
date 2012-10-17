package de.zalando.jpa.types;

// This implementation works only with Postgres and hibernate
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PGEnumType // implements EnhancedUserType, ParameterizedType
{
    /*
     *
     * public static final String TYPE = "de.zalando.jpa.types.PGEnumType";
     * public static final String PARAM = "enum";
     *
     * // Enum  class under observation
     * private Class<Enum> enumClass;
     *
     * @Override
     * public void setParameterValues(final Properties parameters) {
     *  final String enumClassName = parameters.getProperty(PARAM);
     *  try {
     *      enumClass = (Class<Enum>) Class.forName(enumClassName);
     *  } catch (final ClassNotFoundException cnfe) {
     *      throw new HibernateException("Enum class not found", cnfe);
     *  }
     * }
     *
     * @Override
     * public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
     *  return cached;
     * }
     *
     * @Override
     * public Object deepCopy(final Object value) throws HibernateException {
     *  return value;
     * }
     *
     * @Override
     * public Serializable disassemble(final Object value) throws HibernateException {
     *  return (Enum) value;
     * }
     *
     * @Override
     * public boolean equals(final Object x, final Object y) throws HibernateException {
     *  return x == y;
     * }
     *
     * @Override
     * public int hashCode(final Object x) throws HibernateException {
     *  return x.hashCode();
     * }
     *
     * @Override
     * public boolean isMutable() {
     *  return false;
     * }
     *
     * @Override
     * public Class returnedClass() {
     *  return enumClass;
     * }
     *
     * @Override
     * public int[] sqlTypes() {
     *  return new int[] {Types.OTHER};
     * }
     *
     * @Override
     * public Object fromXMLString(final String xmlValue) {
     *  return Enum.valueOf(enumClass, xmlValue);
     * }
     *
     * @Override
     * public String objectToSQLString(final Object value) {
     *  return '\'' + ((Enum) value).name() + '\'';
     * }
     *
     * @Override
     * public String toXMLString(final Object value) {
     *  return ((Enum) value).name();
     * }
     *
     * @Override
     * public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session,
     *      final Object owner) throws HibernateException, SQLException {
     *  final Object object = rs.getObject(names[0]);
     *  if (rs.wasNull()) {
     *      return null;
     *  }
     *
     *  // Notice how Object is mapped to PGobject. This makes this implementation Postgres specific
     *  if (object instanceof PGobject) {
     *      final PGobject pg = (PGobject) object;
     *      return Enum.valueOf(enumClass, pg.getValue());
     *  }
     *
     *  return null;
     * }
     *
     * @Override
     * public void nullSafeSet(final PreparedStatement st, final Object value, final int index,
     *      final SessionImplementor session) throws HibernateException, SQLException {
     *  if (value == null) {
     *      st.setNull(index, Types.OTHER);
     *  } else {
     *      st.setObject(index, (value), Types.OTHER);
     *  }
     * }
     *
     * @Override
     * public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
     *  return original;
     * }
     *
     *
     */
}
