
package emu.grasscutter.net.proto;

public final class TeamMoonPhaseChangeNotifyOuterClass {
  private TeamMoonPhaseChangeNotifyOuterClass() {}
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public interface TeamMoonPhaseChangeNotifyOrBuilder extends com.google.protobuf.MessageOrBuilder {

    int getGCFNIGDBKNG();

    long getMDNLKCGAKBP();

    boolean getFKDPDKHJBHI();

    int getMoonPhaseType();

    int getMoonPhaseLevel();
  }

  public static final class TeamMoonPhaseChangeNotify extends
      com.google.protobuf.GeneratedMessageV3 implements TeamMoonPhaseChangeNotifyOrBuilder {
    private static final long serialVersionUID = 0L;

    private TeamMoonPhaseChangeNotify(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private TeamMoonPhaseChangeNotify() {}

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
      return new TeamMoonPhaseChangeNotify();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }

    private TeamMoonPhaseChangeNotify(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) throw new java.lang.NullPointerException();
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0: done = true; break;
            case 8: { gcfnigdbkng_ = input.readUInt32(); break; }
            case 40: { mdnlkcgakbp_ = input.readUInt64(); break; }
            case 56: { fkdpdkhjbhi_ = input.readBool(); break; }
            case 72: { moonPhaseType_ = input.readUInt32(); break; }
            case 104: { moonPhaseLevel_ = input.readUInt32(); break; }
            default: {
              if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) done = true;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }

    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return null;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return null;
    }

    public static final int GCFNIGDBKNG_FIELD_NUMBER = 1;
    private int gcfnigdbkng_;
    @java.lang.Override public int getGCFNIGDBKNG() { return gcfnigdbkng_; }

    public static final int MDNLKCGAKBP_FIELD_NUMBER = 5;
    private long mdnlkcgakbp_;
    @java.lang.Override public long getMDNLKCGAKBP() { return mdnlkcgakbp_; }

    public static final int FKDPDKHJBHI_FIELD_NUMBER = 7;
    private boolean fkdpdkhjbhi_;
    @java.lang.Override public boolean getFKDPDKHJBHI() { return fkdpdkhjbhi_; }

    public static final int MOON_PHASE_TYPE_FIELD_NUMBER = 9;
    private int moonPhaseType_;
    @java.lang.Override public int getMoonPhaseType() { return moonPhaseType_; }

    public static final int MOON_PHASE_LEVEL_FIELD_NUMBER = 13;
    private int moonPhaseLevel_;
    @java.lang.Override public int getMoonPhaseLevel() { return moonPhaseLevel_; }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
      if (gcfnigdbkng_ != 0) output.writeUInt32(1, gcfnigdbkng_);
      if (mdnlkcgakbp_ != 0L) output.writeUInt64(5, mdnlkcgakbp_);
      if (fkdpdkhjbhi_ != false) output.writeBool(7, fkdpdkhjbhi_);
      if (moonPhaseType_ != 0) output.writeUInt32(9, moonPhaseType_);
      if (moonPhaseLevel_ != 0) output.writeUInt32(13, moonPhaseLevel_);
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;
      size = 0;
      if (gcfnigdbkng_ != 0) size += com.google.protobuf.CodedOutputStream.computeUInt32Size(1, gcfnigdbkng_);
      if (mdnlkcgakbp_ != 0L) size += com.google.protobuf.CodedOutputStream.computeUInt64Size(5, mdnlkcgakbp_);
      if (fkdpdkhjbhi_ != false) size += com.google.protobuf.CodedOutputStream.computeBoolSize(7, fkdpdkhjbhi_);
      if (moonPhaseType_ != 0) size += com.google.protobuf.CodedOutputStream.computeUInt32Size(9, moonPhaseType_);
      if (moonPhaseLevel_ != 0) size += com.google.protobuf.CodedOutputStream.computeUInt32Size(13, moonPhaseLevel_);
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) return true;
      if (!(obj instanceof TeamMoonPhaseChangeNotify)) return super.equals(obj);
      TeamMoonPhaseChangeNotify other = (TeamMoonPhaseChangeNotify) obj;
      if (gcfnigdbkng_ != other.gcfnigdbkng_) return false;
      if (mdnlkcgakbp_ != other.mdnlkcgakbp_) return false;
      if (fkdpdkhjbhi_ != other.fkdpdkhjbhi_) return false;
      if (moonPhaseType_ != other.moonPhaseType_) return false;
      if (moonPhaseLevel_ != other.moonPhaseLevel_) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) return memoizedHashCode;
      int hash = 41;
      hash = (19 * hash) + MOON_PHASE_TYPE_FIELD_NUMBER;
      hash = (37 * hash) + moonPhaseType_;
      hash = (19 * hash) + MOON_PHASE_LEVEL_FIELD_NUMBER;
      hash = (37 * hash) + moonPhaseLevel_;
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static TeamMoonPhaseChangeNotify parseFrom(java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(
        java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(
        com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(
        byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(
        java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static TeamMoonPhaseChangeNotify parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    public static TeamMoonPhaseChangeNotify parseDelimitedFrom(
        java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    public static TeamMoonPhaseChangeNotify parseFrom(
        com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() { return DEFAULT_INSTANCE.toBuilder(); }
    public static Builder newBuilder(TeamMoonPhaseChangeNotify prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      return new Builder(parent);
    }

    public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
        implements TeamMoonPhaseChangeNotifyOrBuilder {

      public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() { return null; }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() { return null; }

      private Builder() { maybeForceBuilderInitialization(); }
      private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {}

      @java.lang.Override
      public Builder clear() {
        super.clear();
        gcfnigdbkng_ = 0;
        mdnlkcgakbp_ = 0L;
        fkdpdkhjbhi_ = false;
        moonPhaseType_ = 0;
        moonPhaseLevel_ = 0;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() { return null; }

      @java.lang.Override
      public TeamMoonPhaseChangeNotify getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }

      @java.lang.Override
      public TeamMoonPhaseChangeNotify build() {
        TeamMoonPhaseChangeNotify result = buildPartial();
        if (!result.isInitialized()) throw newUninitializedMessageException(result);
        return result;
      }

      @java.lang.Override
      public TeamMoonPhaseChangeNotify buildPartial() {
        TeamMoonPhaseChangeNotify result = new TeamMoonPhaseChangeNotify(this);
        result.gcfnigdbkng_ = gcfnigdbkng_;
        result.mdnlkcgakbp_ = mdnlkcgakbp_;
        result.fkdpdkhjbhi_ = fkdpdkhjbhi_;
        result.moonPhaseType_ = moonPhaseType_;
        result.moonPhaseLevel_ = moonPhaseLevel_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() { return super.clone(); }
      @java.lang.Override
      public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) { return super.setField(field, value); }
      @java.lang.Override
      public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) { return super.clearField(field); }
      @java.lang.Override
      public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) { return super.clearOneof(oneof); }
      @java.lang.Override
      public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) { return super.setRepeatedField(field, index, value); }
      @java.lang.Override
      public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) { return super.addRepeatedField(field, value); }

      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof TeamMoonPhaseChangeNotify) return mergeFrom((TeamMoonPhaseChangeNotify) other);
        super.mergeFrom(other);
        return this;
      }

      public Builder mergeFrom(TeamMoonPhaseChangeNotify other) {
        if (other == DEFAULT_INSTANCE) return this;
        if (other.getGCFNIGDBKNG() != 0) setGCFNIGDBKNG(other.getGCFNIGDBKNG());
        if (other.getMDNLKCGAKBP() != 0L) setMDNLKCGAKBP(other.getMDNLKCGAKBP());
        if (other.getFKDPDKHJBHI()) setFKDPDKHJBHI(other.getFKDPDKHJBHI());
        if (other.getMoonPhaseType() != 0) setMoonPhaseType(other.getMoonPhaseType());
        if (other.getMoonPhaseLevel() != 0) setMoonPhaseLevel(other.getMoonPhaseLevel());
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() { return true; }

      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
        TeamMoonPhaseChangeNotify parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (TeamMoonPhaseChangeNotify) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) mergeFrom(parsedMessage);
        }
        return this;
      }

      private int gcfnigdbkng_;
      @java.lang.Override public int getGCFNIGDBKNG() { return gcfnigdbkng_; }
      public Builder setGCFNIGDBKNG(int value) { gcfnigdbkng_ = value; onChanged(); return this; }
      public Builder clearGCFNIGDBKNG() { gcfnigdbkng_ = 0; onChanged(); return this; }

      private long mdnlkcgakbp_;
      @java.lang.Override public long getMDNLKCGAKBP() { return mdnlkcgakbp_; }
      public Builder setMDNLKCGAKBP(long value) { mdnlkcgakbp_ = value; onChanged(); return this; }
      public Builder clearMDNLKCGAKBP() { mdnlkcgakbp_ = 0L; onChanged(); return this; }

      private boolean fkdpdkhjbhi_;
      @java.lang.Override public boolean getFKDPDKHJBHI() { return fkdpdkhjbhi_; }
      public Builder setFKDPDKHJBHI(boolean value) { fkdpdkhjbhi_ = value; onChanged(); return this; }
      public Builder clearFKDPDKHJBHI() { fkdpdkhjbhi_ = false; onChanged(); return this; }

      private int moonPhaseType_;
      @java.lang.Override public int getMoonPhaseType() { return moonPhaseType_; }
      public Builder setMoonPhaseType(int value) { moonPhaseType_ = value; onChanged(); return this; }
      public Builder clearMoonPhaseType() { moonPhaseType_ = 0; onChanged(); return this; }

      private int moonPhaseLevel_;
      @java.lang.Override public int getMoonPhaseLevel() { return moonPhaseLevel_; }
      public Builder setMoonPhaseLevel(int value) { moonPhaseLevel_ = value; onChanged(); return this; }
      public Builder clearMoonPhaseLevel() { moonPhaseLevel_ = 0; onChanged(); return this; }

      @java.lang.Override
      public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }
      @java.lang.Override
      public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }
    }

    private static final TeamMoonPhaseChangeNotify DEFAULT_INSTANCE = new TeamMoonPhaseChangeNotify();
    public static TeamMoonPhaseChangeNotify getDefaultInstance() { return DEFAULT_INSTANCE; }

    private static final com.google.protobuf.Parser<TeamMoonPhaseChangeNotify> PARSER =
        new com.google.protobuf.AbstractParser<TeamMoonPhaseChangeNotify>() {
          @java.lang.Override
          public TeamMoonPhaseChangeNotify parsePartialFrom(
              com.google.protobuf.CodedInputStream input,
              com.google.protobuf.ExtensionRegistryLite extensionRegistry)
              throws com.google.protobuf.InvalidProtocolBufferException {
            return new TeamMoonPhaseChangeNotify(input, extensionRegistry);
          }
        };

    public static com.google.protobuf.Parser<TeamMoonPhaseChangeNotify> parser() { return PARSER; }

    @java.lang.Override
    public com.google.protobuf.Parser<TeamMoonPhaseChangeNotify> getParserForType() { return PARSER; }

    @java.lang.Override
    public TeamMoonPhaseChangeNotify getDefaultInstanceForType() { return DEFAULT_INSTANCE; }
  }

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry, TeamMoonPhaseChangeNotifyOuterClass file) {}
}
