
package emu.grasscutter.net.proto;

public final class QueryPathReqOuterClass {
  private QueryPathReqOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface QueryPathReqOrBuilder extends

      com.google.protobuf.MessageOrBuilder {

    boolean hasSourcePos();

    emu.grasscutter.net.proto.VectorOuterClass.Vector getSourcePos();

    emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder getSourcePosOrBuilder();

    java.util.List<emu.grasscutter.net.proto.VectorOuterClass.Vector>
        getDestinationPosList();

    emu.grasscutter.net.proto.VectorOuterClass.Vector getDestinationPos(int index);

    int getDestinationPosCount();

    java.util.List<? extends emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder>
        getDestinationPosOrBuilderList();

    emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder getDestinationPosOrBuilder(
        int index);

    boolean hasFilter();

    emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter getFilter();

    emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilterOrBuilder getFilterOrBuilder();

    boolean hasMEBHBIIIIND();

    emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int getMEBHBIIIIND();

    emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3IntOrBuilder getMEBHBIIIINDOrBuilder();

    int getQueryTypeValue();

    emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType getQueryType();

    int getSceneId();

    int getQueryId();
  }

  public static final class QueryPathReq extends
      com.google.protobuf.GeneratedMessageV3 implements

      QueryPathReqOrBuilder {
  private static final long serialVersionUID = 0L;

    private QueryPathReq(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private QueryPathReq() {
      destinationPos_ = java.util.Collections.emptyList();
      queryType_ = 0;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new QueryPathReq();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private QueryPathReq(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 26: {
              emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder subBuilder = null;
              if (sourcePos_ != null) {
                subBuilder = sourcePos_.toBuilder();
              }
              sourcePos_ = input.readMessage(emu.grasscutter.net.proto.VectorOuterClass.Vector.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(sourcePos_);
                sourcePos_ = subBuilder.buildPartial();
              }

              break;
            }
            case 8: {

              queryId_ = input.readInt32();
              break;
            }
            case 80: {
              int rawValue = input.readEnum();

              queryType_ = rawValue;
              break;
            }
            case 50: {
              emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.Builder subBuilder = null;
              if (mEBHBIIIIND_ != null) {
                subBuilder = mEBHBIIIIND_.toBuilder();
              }
              mEBHBIIIIND_ = input.readMessage(emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(mEBHBIIIIND_);
                mEBHBIIIIND_ = subBuilder.buildPartial();
              }

              break;
            }
            case 16: {

              sceneId_ = input.readUInt32();
              break;
            }
            case 114: {
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                destinationPos_ = new java.util.ArrayList<emu.grasscutter.net.proto.VectorOuterClass.Vector>();
                mutable_bitField0_ |= 0x00000001;
              }
              destinationPos_.add(
                  input.readMessage(emu.grasscutter.net.proto.VectorOuterClass.Vector.parser(), extensionRegistry));
              break;
            }
            case 66: {
              emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.Builder subBuilder = null;
              if (filter_ != null) {
                subBuilder = filter_.toBuilder();
              }
              filter_ = input.readMessage(emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(filter_);
                filter_ = subBuilder.buildPartial();
              }

              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) != 0)) {
          destinationPos_ = java.util.Collections.unmodifiableList(destinationPos_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return emu.grasscutter.net.proto.QueryPathReqOuterClass.internal_static_QueryPathReq_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return emu.grasscutter.net.proto.QueryPathReqOuterClass.internal_static_QueryPathReq_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq.class, emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq.Builder.class);
    }

    public static final int SOURCE_POS_FIELD_NUMBER = 3;
    private emu.grasscutter.net.proto.VectorOuterClass.Vector sourcePos_;

    @java.lang.Override
    public boolean hasSourcePos() {
      return sourcePos_ != null;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.VectorOuterClass.Vector getSourcePos() {
      return sourcePos_ == null ? emu.grasscutter.net.proto.VectorOuterClass.Vector.getDefaultInstance() : sourcePos_;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder getSourcePosOrBuilder() {
      return getSourcePos();
    }

    public static final int DESTINATION_POS_FIELD_NUMBER = 14;
    private java.util.List<emu.grasscutter.net.proto.VectorOuterClass.Vector> destinationPos_;

    @java.lang.Override
    public java.util.List<emu.grasscutter.net.proto.VectorOuterClass.Vector> getDestinationPosList() {
      return destinationPos_;
    }

    @java.lang.Override
    public java.util.List<? extends emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder>
        getDestinationPosOrBuilderList() {
      return destinationPos_;
    }

    @java.lang.Override
    public int getDestinationPosCount() {
      return destinationPos_.size();
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.VectorOuterClass.Vector getDestinationPos(int index) {
      return destinationPos_.get(index);
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder getDestinationPosOrBuilder(
        int index) {
      return destinationPos_.get(index);
    }

    public static final int FILTER_FIELD_NUMBER = 8;
    private emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter filter_;

    @java.lang.Override
    public boolean hasFilter() {
      return filter_ != null;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter getFilter() {
      return filter_ == null ? emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.getDefaultInstance() : filter_;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilterOrBuilder getFilterOrBuilder() {
      return getFilter();
    }

    public static final int MEBHBIIIIND_FIELD_NUMBER = 6;
    private emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int mEBHBIIIIND_;

    @java.lang.Override
    public boolean hasMEBHBIIIIND() {
      return mEBHBIIIIND_ != null;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int getMEBHBIIIIND() {
      return mEBHBIIIIND_ == null ? emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.getDefaultInstance() : mEBHBIIIIND_;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3IntOrBuilder getMEBHBIIIINDOrBuilder() {
      return getMEBHBIIIIND();
    }

    public static final int QUERY_TYPE_FIELD_NUMBER = 10;
    private int queryType_;

    @java.lang.Override public int getQueryTypeValue() {
      return queryType_;
    }

    @java.lang.Override public emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType getQueryType() {
      @SuppressWarnings("deprecation")
      emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType result = emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType.valueOf(queryType_);
      return result == null ? emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType.UNRECOGNIZED : result;
    }

    public static final int SCENE_ID_FIELD_NUMBER = 2;
    private int sceneId_;

    @java.lang.Override
    public int getSceneId() {
      return sceneId_;
    }

    public static final int QUERY_ID_FIELD_NUMBER = 1;
    private int queryId_;

    @java.lang.Override
    public int getQueryId() {
      return queryId_;
    }

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
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (sourcePos_ != null) {
        output.writeMessage(3, getSourcePos());
      }
      if (queryId_ != 0) {
        output.writeInt32(1, queryId_);
      }
      if (queryType_ != emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType.OptionType_OPTION_NONE.getNumber()) {
        output.writeEnum(10, queryType_);
      }
      if (mEBHBIIIIND_ != null) {
        output.writeMessage(6, getMEBHBIIIIND());
      }
      if (sceneId_ != 0) {
        output.writeUInt32(2, sceneId_);
      }
      for (int i = 0; i < destinationPos_.size(); i++) {
        output.writeMessage(14, destinationPos_.get(i));
      }
      if (filter_ != null) {
        output.writeMessage(8, getFilter());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (sourcePos_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, getSourcePos());
      }
      if (queryId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, queryId_);
      }
      if (queryType_ != emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType.OptionType_OPTION_NONE.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(10, queryType_);
      }
      if (mEBHBIIIIND_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(6, getMEBHBIIIIND());
      }
      if (sceneId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(2, sceneId_);
      }
      for (int i = 0; i < destinationPos_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(14, destinationPos_.get(i));
      }
      if (filter_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(8, getFilter());
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq)) {
        return super.equals(obj);
      }
      emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq other = (emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq) obj;

      if (hasSourcePos() != other.hasSourcePos()) return false;
      if (hasSourcePos()) {
        if (!getSourcePos()
            .equals(other.getSourcePos())) return false;
      }
      if (!getDestinationPosList()
          .equals(other.getDestinationPosList())) return false;
      if (hasFilter() != other.hasFilter()) return false;
      if (hasFilter()) {
        if (!getFilter()
            .equals(other.getFilter())) return false;
      }
      if (hasMEBHBIIIIND() != other.hasMEBHBIIIIND()) return false;
      if (hasMEBHBIIIIND()) {
        if (!getMEBHBIIIIND()
            .equals(other.getMEBHBIIIIND())) return false;
      }
      if (queryType_ != other.queryType_) return false;
      if (getSceneId()
          != other.getSceneId()) return false;
      if (getQueryId()
          != other.getQueryId()) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasSourcePos()) {
        hash = (37 * hash) + SOURCE_POS_FIELD_NUMBER;
        hash = (53 * hash) + getSourcePos().hashCode();
      }
      if (getDestinationPosCount() > 0) {
        hash = (37 * hash) + DESTINATION_POS_FIELD_NUMBER;
        hash = (53 * hash) + getDestinationPosList().hashCode();
      }
      if (hasFilter()) {
        hash = (37 * hash) + FILTER_FIELD_NUMBER;
        hash = (53 * hash) + getFilter().hashCode();
      }
      if (hasMEBHBIIIIND()) {
        hash = (37 * hash) + MEBHBIIIIND_FIELD_NUMBER;
        hash = (53 * hash) + getMEBHBIIIIND().hashCode();
      }
      hash = (37 * hash) + QUERY_TYPE_FIELD_NUMBER;
      hash = (53 * hash) + queryType_;
      hash = (37 * hash) + SCENE_ID_FIELD_NUMBER;
      hash = (53 * hash) + getSceneId();
      hash = (37 * hash) + QUERY_ID_FIELD_NUMBER;
      hash = (53 * hash) + getQueryId();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }

    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements

        emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReqOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return emu.grasscutter.net.proto.QueryPathReqOuterClass.internal_static_QueryPathReq_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return emu.grasscutter.net.proto.QueryPathReqOuterClass.internal_static_QueryPathReq_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq.class, emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq.Builder.class);
      }

      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getDestinationPosFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        if (sourcePosBuilder_ == null) {
          sourcePos_ = null;
        } else {
          sourcePos_ = null;
          sourcePosBuilder_ = null;
        }
        if (destinationPosBuilder_ == null) {
          destinationPos_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          destinationPosBuilder_.clear();
        }
        if (filterBuilder_ == null) {
          filter_ = null;
        } else {
          filter_ = null;
          filterBuilder_ = null;
        }
        if (mEBHBIIIINDBuilder_ == null) {
          mEBHBIIIIND_ = null;
        } else {
          mEBHBIIIIND_ = null;
          mEBHBIIIINDBuilder_ = null;
        }
        queryType_ = 0;

        sceneId_ = 0;

        queryId_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return emu.grasscutter.net.proto.QueryPathReqOuterClass.internal_static_QueryPathReq_descriptor;
      }

      @java.lang.Override
      public emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq getDefaultInstanceForType() {
        return emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq.getDefaultInstance();
      }

      @java.lang.Override
      public emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq build() {
        emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq buildPartial() {
        emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq result = new emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq(this);
        int from_bitField0_ = bitField0_;
        if (sourcePosBuilder_ == null) {
          result.sourcePos_ = sourcePos_;
        } else {
          result.sourcePos_ = sourcePosBuilder_.build();
        }
        if (destinationPosBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0)) {
            destinationPos_ = java.util.Collections.unmodifiableList(destinationPos_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.destinationPos_ = destinationPos_;
        } else {
          result.destinationPos_ = destinationPosBuilder_.build();
        }
        if (filterBuilder_ == null) {
          result.filter_ = filter_;
        } else {
          result.filter_ = filterBuilder_.build();
        }
        if (mEBHBIIIINDBuilder_ == null) {
          result.mEBHBIIIIND_ = mEBHBIIIIND_;
        } else {
          result.mEBHBIIIIND_ = mEBHBIIIINDBuilder_.build();
        }
        result.queryType_ = queryType_;
        result.sceneId_ = sceneId_;
        result.queryId_ = queryId_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq) {
          return mergeFrom((emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq other) {
        if (other == emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq.getDefaultInstance()) return this;
        if (other.hasSourcePos()) {
          mergeSourcePos(other.getSourcePos());
        }
        if (destinationPosBuilder_ == null) {
          if (!other.destinationPos_.isEmpty()) {
            if (destinationPos_.isEmpty()) {
              destinationPos_ = other.destinationPos_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureDestinationPosIsMutable();
              destinationPos_.addAll(other.destinationPos_);
            }
            onChanged();
          }
        } else {
          if (!other.destinationPos_.isEmpty()) {
            if (destinationPosBuilder_.isEmpty()) {
              destinationPosBuilder_.dispose();
              destinationPosBuilder_ = null;
              destinationPos_ = other.destinationPos_;
              bitField0_ = (bitField0_ & ~0x00000001);
              destinationPosBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getDestinationPosFieldBuilder() : null;
            } else {
              destinationPosBuilder_.addAllMessages(other.destinationPos_);
            }
          }
        }
        if (other.hasFilter()) {
          mergeFilter(other.getFilter());
        }
        if (other.hasMEBHBIIIIND()) {
          mergeMEBHBIIIIND(other.getMEBHBIIIIND());
        }
        if (other.queryType_ != 0) {
          setQueryTypeValue(other.getQueryTypeValue());
        }
        if (other.getSceneId() != 0) {
          setSceneId(other.getSceneId());
        }
        if (other.getQueryId() != 0) {
          setQueryId(other.getQueryId());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private emu.grasscutter.net.proto.VectorOuterClass.Vector sourcePos_;
      private com.google.protobuf.SingleFieldBuilderV3<
          emu.grasscutter.net.proto.VectorOuterClass.Vector, emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder, emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder> sourcePosBuilder_;

      public boolean hasSourcePos() {
        return sourcePosBuilder_ != null || sourcePos_ != null;
      }

      public emu.grasscutter.net.proto.VectorOuterClass.Vector getSourcePos() {
        if (sourcePosBuilder_ == null) {
          return sourcePos_ == null ? emu.grasscutter.net.proto.VectorOuterClass.Vector.getDefaultInstance() : sourcePos_;
        } else {
          return sourcePosBuilder_.getMessage();
        }
      }

      public Builder setSourcePos(emu.grasscutter.net.proto.VectorOuterClass.Vector value) {
        if (sourcePosBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          sourcePos_ = value;
          onChanged();
        } else {
          sourcePosBuilder_.setMessage(value);
        }

        return this;
      }

      public Builder setSourcePos(
          emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder builderForValue) {
        if (sourcePosBuilder_ == null) {
          sourcePos_ = builderForValue.build();
          onChanged();
        } else {
          sourcePosBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }

      public Builder mergeSourcePos(emu.grasscutter.net.proto.VectorOuterClass.Vector value) {
        if (sourcePosBuilder_ == null) {
          if (sourcePos_ != null) {
            sourcePos_ =
              emu.grasscutter.net.proto.VectorOuterClass.Vector.newBuilder(sourcePos_).mergeFrom(value).buildPartial();
          } else {
            sourcePos_ = value;
          }
          onChanged();
        } else {
          sourcePosBuilder_.mergeFrom(value);
        }

        return this;
      }

      public Builder clearSourcePos() {
        if (sourcePosBuilder_ == null) {
          sourcePos_ = null;
          onChanged();
        } else {
          sourcePos_ = null;
          sourcePosBuilder_ = null;
        }

        return this;
      }

      public emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder getSourcePosBuilder() {

        onChanged();
        return getSourcePosFieldBuilder().getBuilder();
      }

      public emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder getSourcePosOrBuilder() {
        if (sourcePosBuilder_ != null) {
          return sourcePosBuilder_.getMessageOrBuilder();
        } else {
          return sourcePos_ == null ?
              emu.grasscutter.net.proto.VectorOuterClass.Vector.getDefaultInstance() : sourcePos_;
        }
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          emu.grasscutter.net.proto.VectorOuterClass.Vector, emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder, emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder>
          getSourcePosFieldBuilder() {
        if (sourcePosBuilder_ == null) {
          sourcePosBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              emu.grasscutter.net.proto.VectorOuterClass.Vector, emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder, emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder>(
                  getSourcePos(),
                  getParentForChildren(),
                  isClean());
          sourcePos_ = null;
        }
        return sourcePosBuilder_;
      }

      private java.util.List<emu.grasscutter.net.proto.VectorOuterClass.Vector> destinationPos_ =
        java.util.Collections.emptyList();
      private void ensureDestinationPosIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          destinationPos_ = new java.util.ArrayList<emu.grasscutter.net.proto.VectorOuterClass.Vector>(destinationPos_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          emu.grasscutter.net.proto.VectorOuterClass.Vector, emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder, emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder> destinationPosBuilder_;

      public java.util.List<emu.grasscutter.net.proto.VectorOuterClass.Vector> getDestinationPosList() {
        if (destinationPosBuilder_ == null) {
          return java.util.Collections.unmodifiableList(destinationPos_);
        } else {
          return destinationPosBuilder_.getMessageList();
        }
      }

      public int getDestinationPosCount() {
        if (destinationPosBuilder_ == null) {
          return destinationPos_.size();
        } else {
          return destinationPosBuilder_.getCount();
        }
      }

      public emu.grasscutter.net.proto.VectorOuterClass.Vector getDestinationPos(int index) {
        if (destinationPosBuilder_ == null) {
          return destinationPos_.get(index);
        } else {
          return destinationPosBuilder_.getMessage(index);
        }
      }

      public Builder setDestinationPos(
          int index, emu.grasscutter.net.proto.VectorOuterClass.Vector value) {
        if (destinationPosBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureDestinationPosIsMutable();
          destinationPos_.set(index, value);
          onChanged();
        } else {
          destinationPosBuilder_.setMessage(index, value);
        }
        return this;
      }

      public Builder setDestinationPos(
          int index, emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder builderForValue) {
        if (destinationPosBuilder_ == null) {
          ensureDestinationPosIsMutable();
          destinationPos_.set(index, builderForValue.build());
          onChanged();
        } else {
          destinationPosBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }

      public Builder addDestinationPos(emu.grasscutter.net.proto.VectorOuterClass.Vector value) {
        if (destinationPosBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureDestinationPosIsMutable();
          destinationPos_.add(value);
          onChanged();
        } else {
          destinationPosBuilder_.addMessage(value);
        }
        return this;
      }

      public Builder addDestinationPos(
          int index, emu.grasscutter.net.proto.VectorOuterClass.Vector value) {
        if (destinationPosBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureDestinationPosIsMutable();
          destinationPos_.add(index, value);
          onChanged();
        } else {
          destinationPosBuilder_.addMessage(index, value);
        }
        return this;
      }

      public Builder addDestinationPos(
          emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder builderForValue) {
        if (destinationPosBuilder_ == null) {
          ensureDestinationPosIsMutable();
          destinationPos_.add(builderForValue.build());
          onChanged();
        } else {
          destinationPosBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }

      public Builder addDestinationPos(
          int index, emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder builderForValue) {
        if (destinationPosBuilder_ == null) {
          ensureDestinationPosIsMutable();
          destinationPos_.add(index, builderForValue.build());
          onChanged();
        } else {
          destinationPosBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }

      public Builder addAllDestinationPos(
          java.lang.Iterable<? extends emu.grasscutter.net.proto.VectorOuterClass.Vector> values) {
        if (destinationPosBuilder_ == null) {
          ensureDestinationPosIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, destinationPos_);
          onChanged();
        } else {
          destinationPosBuilder_.addAllMessages(values);
        }
        return this;
      }

      public Builder clearDestinationPos() {
        if (destinationPosBuilder_ == null) {
          destinationPos_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          destinationPosBuilder_.clear();
        }
        return this;
      }

      public Builder removeDestinationPos(int index) {
        if (destinationPosBuilder_ == null) {
          ensureDestinationPosIsMutable();
          destinationPos_.remove(index);
          onChanged();
        } else {
          destinationPosBuilder_.remove(index);
        }
        return this;
      }

      public emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder getDestinationPosBuilder(
          int index) {
        return getDestinationPosFieldBuilder().getBuilder(index);
      }

      public emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder getDestinationPosOrBuilder(
          int index) {
        if (destinationPosBuilder_ == null) {
          return destinationPos_.get(index);  } else {
          return destinationPosBuilder_.getMessageOrBuilder(index);
        }
      }

      public java.util.List<? extends emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder>
           getDestinationPosOrBuilderList() {
        if (destinationPosBuilder_ != null) {
          return destinationPosBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(destinationPos_);
        }
      }

      public emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder addDestinationPosBuilder() {
        return getDestinationPosFieldBuilder().addBuilder(
            emu.grasscutter.net.proto.VectorOuterClass.Vector.getDefaultInstance());
      }

      public emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder addDestinationPosBuilder(
          int index) {
        return getDestinationPosFieldBuilder().addBuilder(
            index, emu.grasscutter.net.proto.VectorOuterClass.Vector.getDefaultInstance());
      }

      public java.util.List<emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder>
           getDestinationPosBuilderList() {
        return getDestinationPosFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          emu.grasscutter.net.proto.VectorOuterClass.Vector, emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder, emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder>
          getDestinationPosFieldBuilder() {
        if (destinationPosBuilder_ == null) {
          destinationPosBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              emu.grasscutter.net.proto.VectorOuterClass.Vector, emu.grasscutter.net.proto.VectorOuterClass.Vector.Builder, emu.grasscutter.net.proto.VectorOuterClass.VectorOrBuilder>(
                  destinationPos_,
                  ((bitField0_ & 0x00000001) != 0),
                  getParentForChildren(),
                  isClean());
          destinationPos_ = null;
        }
        return destinationPosBuilder_;
      }

      private emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter filter_;
      private com.google.protobuf.SingleFieldBuilderV3<
          emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter, emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.Builder, emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilterOrBuilder> filterBuilder_;

      public boolean hasFilter() {
        return filterBuilder_ != null || filter_ != null;
      }

      public emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter getFilter() {
        if (filterBuilder_ == null) {
          return filter_ == null ? emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.getDefaultInstance() : filter_;
        } else {
          return filterBuilder_.getMessage();
        }
      }

      public Builder setFilter(emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter value) {
        if (filterBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          filter_ = value;
          onChanged();
        } else {
          filterBuilder_.setMessage(value);
        }

        return this;
      }

      public Builder setFilter(
          emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.Builder builderForValue) {
        if (filterBuilder_ == null) {
          filter_ = builderForValue.build();
          onChanged();
        } else {
          filterBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }

      public Builder mergeFilter(emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter value) {
        if (filterBuilder_ == null) {
          if (filter_ != null) {
            filter_ =
              emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.newBuilder(filter_).mergeFrom(value).buildPartial();
          } else {
            filter_ = value;
          }
          onChanged();
        } else {
          filterBuilder_.mergeFrom(value);
        }

        return this;
      }

      public Builder clearFilter() {
        if (filterBuilder_ == null) {
          filter_ = null;
          onChanged();
        } else {
          filter_ = null;
          filterBuilder_ = null;
        }

        return this;
      }

      public emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.Builder getFilterBuilder() {

        onChanged();
        return getFilterFieldBuilder().getBuilder();
      }

      public emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilterOrBuilder getFilterOrBuilder() {
        if (filterBuilder_ != null) {
          return filterBuilder_.getMessageOrBuilder();
        } else {
          return filter_ == null ?
              emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.getDefaultInstance() : filter_;
        }
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter, emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.Builder, emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilterOrBuilder>
          getFilterFieldBuilder() {
        if (filterBuilder_ == null) {
          filterBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter, emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilter.Builder, emu.grasscutter.net.proto.QueryFilterOuterClass.QueryFilterOrBuilder>(
                  getFilter(),
                  getParentForChildren(),
                  isClean());
          filter_ = null;
        }
        return filterBuilder_;
      }

      private emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int mEBHBIIIIND_;
      private com.google.protobuf.SingleFieldBuilderV3<
          emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int, emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.Builder, emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3IntOrBuilder> mEBHBIIIINDBuilder_;

      public boolean hasMEBHBIIIIND() {
        return mEBHBIIIINDBuilder_ != null || mEBHBIIIIND_ != null;
      }

      public emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int getMEBHBIIIIND() {
        if (mEBHBIIIINDBuilder_ == null) {
          return mEBHBIIIIND_ == null ? emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.getDefaultInstance() : mEBHBIIIIND_;
        } else {
          return mEBHBIIIINDBuilder_.getMessage();
        }
      }

      public Builder setMEBHBIIIIND(emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int value) {
        if (mEBHBIIIINDBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          mEBHBIIIIND_ = value;
          onChanged();
        } else {
          mEBHBIIIINDBuilder_.setMessage(value);
        }

        return this;
      }

      public Builder setMEBHBIIIIND(
          emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.Builder builderForValue) {
        if (mEBHBIIIINDBuilder_ == null) {
          mEBHBIIIIND_ = builderForValue.build();
          onChanged();
        } else {
          mEBHBIIIINDBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }

      public Builder mergeMEBHBIIIIND(emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int value) {
        if (mEBHBIIIINDBuilder_ == null) {
          if (mEBHBIIIIND_ != null) {
            mEBHBIIIIND_ =
              emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.newBuilder(mEBHBIIIIND_).mergeFrom(value).buildPartial();
          } else {
            mEBHBIIIIND_ = value;
          }
          onChanged();
        } else {
          mEBHBIIIINDBuilder_.mergeFrom(value);
        }

        return this;
      }

      public Builder clearMEBHBIIIIND() {
        if (mEBHBIIIINDBuilder_ == null) {
          mEBHBIIIIND_ = null;
          onChanged();
        } else {
          mEBHBIIIIND_ = null;
          mEBHBIIIINDBuilder_ = null;
        }

        return this;
      }

      public emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.Builder getMEBHBIIIINDBuilder() {

        onChanged();
        return getMEBHBIIIINDFieldBuilder().getBuilder();
      }

      public emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3IntOrBuilder getMEBHBIIIINDOrBuilder() {
        if (mEBHBIIIINDBuilder_ != null) {
          return mEBHBIIIINDBuilder_.getMessageOrBuilder();
        } else {
          return mEBHBIIIIND_ == null ?
              emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.getDefaultInstance() : mEBHBIIIIND_;
        }
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int, emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.Builder, emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3IntOrBuilder>
          getMEBHBIIIINDFieldBuilder() {
        if (mEBHBIIIINDBuilder_ == null) {
          mEBHBIIIINDBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int, emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3Int.Builder, emu.grasscutter.net.proto.Vector3IntOuterClass.Vector3IntOrBuilder>(
                  getMEBHBIIIIND(),
                  getParentForChildren(),
                  isClean());
          mEBHBIIIIND_ = null;
        }
        return mEBHBIIIINDBuilder_;
      }

      private int queryType_ = 0;

      @java.lang.Override public int getQueryTypeValue() {
        return queryType_;
      }

      public Builder setQueryTypeValue(int value) {

        queryType_ = value;
        onChanged();
        return this;
      }

      @java.lang.Override
      public emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType getQueryType() {
        @SuppressWarnings("deprecation")
        emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType result = emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType.valueOf(queryType_);
        return result == null ? emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType.UNRECOGNIZED : result;
      }

      public Builder setQueryType(emu.grasscutter.net.proto.OptionTypeOuterClass.OptionType value) {
        if (value == null) {
          throw new NullPointerException();
        }

        queryType_ = value.getNumber();
        onChanged();
        return this;
      }

      public Builder clearQueryType() {

        queryType_ = 0;
        onChanged();
        return this;
      }

      private int sceneId_ ;

      @java.lang.Override
      public int getSceneId() {
        return sceneId_;
      }

      public Builder setSceneId(int value) {

        sceneId_ = value;
        onChanged();
        return this;
      }

      public Builder clearSceneId() {

        sceneId_ = 0;
        onChanged();
        return this;
      }

      private int queryId_ ;

      @java.lang.Override
      public int getQueryId() {
        return queryId_;
      }

      public Builder setQueryId(int value) {

        queryId_ = value;
        onChanged();
        return this;
      }

      public Builder clearQueryId() {

        queryId_ = 0;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }

    }

    private static final emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq();
    }

    public static emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<QueryPathReq>
        PARSER = new com.google.protobuf.AbstractParser<QueryPathReq>() {
      @java.lang.Override
      public QueryPathReq parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new QueryPathReq(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<QueryPathReq> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<QueryPathReq> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.QueryPathReqOuterClass.QueryPathReq getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_QueryPathReq_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_QueryPathReq_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\022QueryPathReq.proto\032\014Vector.proto\032\021Quer" +
      "yFilter.proto\032\020Vector3Int.proto\032\020OptionT" +
      "ype.proto\"\322\001\n\014QueryPathReq\022\033\n\nsource_pos" +
      "\030\001 \001(\0132\007.Vector\022 \n\017destination_pos\030\013 \003(\013" +
      "2\007.Vector\022\034\n\006filter\030\016 \001(\0132\014.QueryFilter\022" +
      " \n\013MEBHBIIIIND\030\006 \001(\0132\013.Vector3Int\022\037\n\nque" +
      "ry_type\030\005 \001(\0162\013.OptionType\022\020\n\010scene_id\030\t" +
      " \001(\r\022\020\n\010query_id\030\003 \001(\005B\033\n\031emu.grasscutte" +
      "r.net.protob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          emu.grasscutter.net.proto.VectorOuterClass.getDescriptor(),
          emu.grasscutter.net.proto.QueryFilterOuterClass.getDescriptor(),
          emu.grasscutter.net.proto.Vector3IntOuterClass.getDescriptor(),
          emu.grasscutter.net.proto.OptionTypeOuterClass.getDescriptor(),
        });
    internal_static_QueryPathReq_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_QueryPathReq_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_QueryPathReq_descriptor,
        new java.lang.String[] { "SourcePos", "DestinationPos", "Filter", "MEBHBIIIIND", "QueryType", "SceneId", "QueryId", });
    emu.grasscutter.net.proto.VectorOuterClass.getDescriptor();
    emu.grasscutter.net.proto.QueryFilterOuterClass.getDescriptor();
    emu.grasscutter.net.proto.Vector3IntOuterClass.getDescriptor();
    emu.grasscutter.net.proto.OptionTypeOuterClass.getDescriptor();
  }

}
